package dev.latvian.kubejs.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.core.RecipeManagerKJS;
import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.item.EmptyItemStackJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.recipe.filter.RecipeFilter;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.server.ServerSettings;
import dev.latvian.kubejs.util.JsonUtilsJS;
import dev.latvian.kubejs.util.ListJS;
import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.util.DynamicMap;
import me.shedaniel.architectury.ExpectPlatform;
import me.shedaniel.architectury.registry.Registries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author LatvianModder
 */
public class RecipeEventJS extends EventJS
{
	public static RecipeEventJS instance;

	public final Map<ResourceLocation, RecipeTypeJS> typeMap;
	public final List<RecipeJS> originalRecipes;
	private final List<RecipeJS> addedRecipes;
	private final Set<RecipeJS> removedRecipes;
	private final Map<ResourceLocation, RecipeFunction> functionMap;

	private final DynamicMap<DynamicMap<RecipeFunction>> recipeFunctions;

	public RecipeEventJS(Map<ResourceLocation, RecipeTypeJS> t)
	{
		typeMap = t;
		originalRecipes = new ArrayList<>();

		ScriptType.SERVER.console.info("Scanning recipes...");

		addedRecipes = new ArrayList<>();
		removedRecipes = new HashSet<>();
		functionMap = new HashMap<>();
		recipeFunctions = new DynamicMap<>(n -> new DynamicMap<>(p -> getRecipeFunction(new ResourceLocation(n, p))));
	}

	public void post(RecipeManager recipeManager, Map<ResourceLocation, JsonObject> jsonMap)
	{
		ScriptType.SERVER.console.setLineNumber(true);

		for (Map.Entry<ResourceLocation, JsonObject> entry : jsonMap.entrySet())
		{
			ResourceLocation recipeId = entry.getKey();

			if (recipeId.getPath().startsWith("_"))
			{
				continue; //Forge: filter anything beginning with "_" as it's used for metadata.
			}

			JsonObject json = entry.getValue();

			try
			{
				if (!processConditions(json, "conditions"))
				{
					ScriptType.SERVER.console.info("Skipping loading recipe " + recipeId + " as it's conditions were not met");
					continue;
				}

				JsonElement t = json.get("type");

				if (!(t instanceof JsonPrimitive) || !((JsonPrimitive) t).isString())
				{
					ScriptType.SERVER.console.warn("Missing or invalid recipe recipe type, expected a string in recipe " + recipeId);
					continue;
				}

				RecipeFunction function = getRecipeFunction(new ResourceLocation(t.getAsString()));

				if (function.type == null)
				{
					ScriptType.SERVER.console.warn("Skipping loading recipe " + recipeId + " as it's type " + function.typeID + " is unknown");
					continue;
				}

				RecipeJS r = function.type.factory.get();
				r.id = recipeId;
				r.type = function.type;
				r.json = json;
				r.originalRecipe = function.type.serializer.fromJson(recipeId, json);

				if (r.originalRecipe == null)
				{
					ScriptType.SERVER.console.warn("Skipping loading recipe " + r + " as it's serializer returned null");
					continue;
				}

				r.deserialize();
				originalRecipes.add(r);

				if (r.originalRecipe.isSpecial())
				{
					ScriptType.SERVER.console.debug("Loaded recipe " + r + ": <dynamic>");
				}
				else
				{
					ScriptType.SERVER.console.debug("Loaded recipe " + r + ": " + r.inputItems + " -> " + r.outputItems);
				}
			}
			catch (Exception ex)
			{
				ScriptType.SERVER.console.error("Parsing error loading recipe " + recipeId + ": " + ex);
			}
		}

		ScriptType.SERVER.console.info("Found " + originalRecipes.size() + " recipes");
		ScriptType.SERVER.console.setLineNumber(true);
		post(ScriptType.SERVER, KubeJSEvents.RECIPES);
		ScriptType.SERVER.console.setLineNumber(false);

		Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> newRecipeMap = new HashMap<>();
		int removed = 0;
		int modified = 0;
		int added = 0;

		for (RecipeJS r : originalRecipes)
		{
			if (removedRecipes.contains(r))
			{
				removed++;
				continue;
			}

			if (r.originalRecipe == null)
			{
				try
				{
					r.serialize();
					r.originalRecipe = r.type.serializer.fromJson(r.id, r.json);
					modified++;
				}
				catch (Exception ex)
				{
					ScriptType.SERVER.console.warn("Error parsing recipe " + r + ": " + ex);
				}
			}

			if (r.originalRecipe != null)
			{
				newRecipeMap.computeIfAbsent(r.originalRecipe.getType(), type -> new HashMap<>()).put(r.id, r.originalRecipe);
			}
		}

		for (RecipeJS r : addedRecipes)
		{
			try
			{
				r.serialize();
				r.originalRecipe = r.type.serializer.fromJson(r.id, r.json);
				added++;
				newRecipeMap.computeIfAbsent(r.originalRecipe.getType(), type -> new HashMap<>()).put(r.id, r.originalRecipe);
			}
			catch (Exception ex)
			{
				ScriptType.SERVER.console.warn("Error creating recipe " + r + ": " + ex);
			}
		}

		((RecipeManagerKJS) recipeManager).setRecipesKJS(newRecipeMap);
		ScriptType.SERVER.console.info("Added " + added + " recipes, removed " + removed + " recipes, modified " + modified + " recipes");
	}

	public DynamicMap<DynamicMap<RecipeFunction>> getRecipes()
	{
		return recipeFunctions;
	}

	public RecipeJS addRecipe(RecipeJS r, RecipeTypeJS type, ListJS args1)
	{
		addedRecipes.add(r);

		if (r.id == null)
		{
			ResourceLocation itemId = UtilsJS.getMCID(r.outputItems.isEmpty() ? EmptyItemStackJS.INSTANCE.getId() : r.outputItems.get(0).getId());
			r.id = new ResourceLocation(Registries.getId(type.serializer, Registry.RECIPE_SERIALIZER_REGISTRY).getNamespace(), "kubejs_generated_" + addedRecipes.size() + "_" + itemId.getNamespace() + "_" + itemId.getPath().replace('/', '_'));
		}

		if (ServerSettings.instance.logAddedRecipes)
		{
			ScriptType.SERVER.console.info("+ " + r + ": " + r.inputItems + " -> " + r.outputItems);
		}
		else
		{
			ScriptType.SERVER.console.debug("+ " + r + ": " + r.inputItems + " -> " + r.outputItems);
		}

		return r;
	}

	public RecipeFilter customFilter(RecipeFilter filter)
	{
		return filter;
	}

	public void forEachRecipe(@Nullable Object o, Consumer<RecipeJS> consumer)
	{
		RecipeFilter filter = RecipeFilter.of(o);

		if (filter == RecipeFilter.ALWAYS_TRUE)
		{
			originalRecipes.forEach(consumer);
		}
		else if (filter != RecipeFilter.ALWAYS_FALSE)
		{
			originalRecipes.stream().filter(filter).forEach(consumer);
		}
	}

	public int remove(Object filter)
	{
		int[] count = new int[1];
		forEachRecipe(filter, r ->
		{
			if (removedRecipes.add(r))
			{
				if (ServerSettings.instance.logRemovedRecipes)
				{
					ScriptType.SERVER.console.info("- " + r + ": " + r.inputItems + " -> " + r.outputItems);
				}
				else
				{
					ScriptType.SERVER.console.debug("- " + r + ": " + r.inputItems + " -> " + r.outputItems);
				}

				count[0]++;
			}
		});
		return count[0];
	}

	public int replaceInput(Object filter, Object ingredient, Object with, boolean exact)
	{
		int[] count = new int[1];
		IngredientJS i = IngredientJS.of(ingredient);
		IngredientJS w = IngredientJS.of(with);
		String is = i.toString();
		String ws = w.toString();
		forEachRecipe(filter, r ->
		{
			if (r.replaceInput(i, w, exact))
			{
				count[0]++;

				if (ServerSettings.instance.logAddedRecipes || ServerSettings.instance.logRemovedRecipes)
				{
					ScriptType.SERVER.console.info("~ " + r + ": OUT " + is + " -> " + ws);
				}
			}
		});
		return count[0];
	}

	public int replaceInput(Object filter, Object ingredient, Object with)
	{
		return replaceInput(filter, ingredient, with, false);
	}

	public int replaceInput(Object ingredient, Object with)
	{
		return replaceInput(RecipeFilter.ALWAYS_TRUE, ingredient, with);
	}

	public int replaceOutput(Object filter, Object ingredient, Object with, boolean exact)
	{
		int[] count = new int[1];
		IngredientJS i = IngredientJS.of(ingredient);
		ItemStackJS w = ItemStackJS.of(with);
		String is = i.toString();
		String ws = w.toString();
		forEachRecipe(filter, r ->
		{
			if (r.replaceOutput(i, w, exact))
			{
				count[0]++;

				if (ServerSettings.instance.logAddedRecipes || ServerSettings.instance.logRemovedRecipes)
				{
					ScriptType.SERVER.console.info("~ " + r + ": IN " + is + " -> " + ws);
				}
			}
		});
		return count[0];
	}

	public int replaceOutput(Object filter, Object ingredient, Object with)
	{
		return replaceOutput(filter, ingredient, with, false);
	}

	public int replaceOutput(Object ingredient, Object with)
	{
		return replaceOutput(RecipeFilter.ALWAYS_TRUE, ingredient, with);
	}

	public RecipeFunction getRecipeFunction(@Nullable ResourceLocation id)
	{
		if (id == null)
		{
			throw new NullPointerException("Recipe type is null!");
		}

		return functionMap.computeIfAbsent(id, location ->
		{
			RecipeSerializer<?> serializer = Registries.get(KubeJS.MOD_ID).get(Registry.RECIPE_SERIALIZER_REGISTRY).get(location);

			if (serializer != null)
			{
				RecipeTypeJS typeJS = typeMap.get(Registries.getId(serializer, Registry.RECIPE_SERIALIZER_REGISTRY));
				return new RecipeFunction(this, location, typeJS != null ? typeJS : new CustomRecipeTypeJS(serializer));
			}
			else
			{
				return new RecipeFunction(this, location, null);
			}
		});
	}

	public RecipeFunction getShaped()
	{
		return getRecipeFunction(Registries.getId(RecipeSerializer.SHAPED_RECIPE, Registry.RECIPE_SERIALIZER_REGISTRY));
	}

	public RecipeFunction getShapeless()
	{
		return getRecipeFunction(Registries.getId(RecipeSerializer.SHAPELESS_RECIPE, Registry.RECIPE_SERIALIZER_REGISTRY));
	}

	public RecipeFunction getSmelting()
	{
		return getRecipeFunction(Registries.getId(RecipeSerializer.SMELTING_RECIPE, Registry.RECIPE_SERIALIZER_REGISTRY));
	}

	public RecipeFunction getBlasting()
	{
		return getRecipeFunction(Registries.getId(RecipeSerializer.BLASTING_RECIPE, Registry.RECIPE_SERIALIZER_REGISTRY));
	}

	public RecipeFunction getSmoking()
	{
		return getRecipeFunction(Registries.getId(RecipeSerializer.SMOKING_RECIPE, Registry.RECIPE_SERIALIZER_REGISTRY));
	}

	public RecipeFunction getStonecutter()
	{
		return getRecipeFunction(Registries.getId(RecipeSerializer.STONECUTTER, Registry.RECIPE_SERIALIZER_REGISTRY));
	}

	public RecipeFunction getSmithing()
	{
		return getRecipeFunction(Registries.getId(RecipeSerializer.SMITHING, Registry.RECIPE_SERIALIZER_REGISTRY));
	}

	public void printTypes()
	{
		ScriptType.SERVER.console.info("== All recipe types ==");
		HashSet<String> list = new HashSet<>();
		originalRecipes.forEach(r -> list.add(r.type.toString()));
		list.stream().sorted().forEach(ScriptType.SERVER.console::info);
		ScriptType.SERVER.console.info(list.size() + " types");
	}

	public void printExamples(String type)
	{
		List<RecipeJS> list = originalRecipes.stream().filter(recipeJS -> recipeJS.type.toString().equals(type)).collect(Collectors.toList());
		Collections.shuffle(list);

		ScriptType.SERVER.console.info("== Random examples of '" + type + "' ==");

		for (int i = 0; i < Math.min(list.size(), 5); i++)
		{
			RecipeJS r = list.get(i);
			ScriptType.SERVER.console.info("- " + r.id + ":\n" + JsonUtilsJS.toPrettyString(r.json));
		}
	}

	@ExpectPlatform
	private static boolean processConditions(JsonObject json, String key)
	{
		throw new AssertionError();
	}
}