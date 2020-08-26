package dev.latvian.kubejs.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.core.RecipeManagerKJS;
import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.item.EmptyItemStackJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.server.ServerSettings;
import dev.latvian.kubejs.util.DynamicMapJS;
import dev.latvian.kubejs.util.JsonUtilsJS;
import dev.latvian.kubejs.util.ListJS;
import dev.latvian.kubejs.util.MapJS;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author LatvianModder
 */
public class RecipeEventJS extends EventJS
{
	public static RecipeEventJS instance;

	private static final Predicate<RecipeJS> ALWAYS_TRUE = r -> true;
	private static final Predicate<RecipeJS> ALWAYS_FALSE = r -> false;

	public final Map<ResourceLocation, RecipeTypeJS> typeMap;
	public final List<RecipeJS> originalRecipes;
	private final List<RecipeJS> addedRecipes;
	private final Set<RecipeJS> removedRecipes;
	public final DynamicMapJS<ResourceLocation, RecipeFunction> functionMap;

	private final DynamicMapJS<String, DynamicMapJS<String, RecipeFunction>> recipeFunctions;

	public RecipeEventJS(Map<ResourceLocation, RecipeTypeJS> t)
	{
		typeMap = t;
		originalRecipes = new ArrayList<>();

		ScriptType.SERVER.console.info("Scanning recipes...");

		addedRecipes = new ArrayList<>();
		removedRecipes = new HashSet<>();
		functionMap = new DynamicMapJS<>(id -> {
			IRecipeSerializer<?> serializer = ForgeRegistries.RECIPE_SERIALIZERS.getValue(id);

			if (serializer != null)
			{
				RecipeTypeJS typeJS = typeMap.get(serializer.getRegistryName());
				return new RecipeFunction(this, id, typeJS != null ? typeJS : new CustomRecipeTypeJS(serializer));
			}
			else
			{
				return new RecipeFunction(this, id, null);
			}
		});

		recipeFunctions = new DynamicMapJS<>(n -> new DynamicMapJS<>(p -> functionMap.get(new ResourceLocation(n, p))));
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
				if (!CraftingHelper.processConditions(json, "conditions"))
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

				RecipeFunction function = functionMap.get(new ResourceLocation(t.getAsString()));

				if (function.type == null)
				{
					ScriptType.SERVER.console.warn("Skipping loading recipe " + recipeId + " as it's type " + function.typeID + " is unknown");
					continue;
				}

				RecipeJS r = function.type.factory.get();
				r.id = recipeId;
				r.type = function.type;
				r.json = json;
				r.originalRecipe = function.type.serializer.read(recipeId, json);

				if (r.originalRecipe == null)
				{
					ScriptType.SERVER.console.warn("Skipping loading recipe " + r + " as it's serializer returned null");
					continue;
				}

				r.deserialize();
				originalRecipes.add(r);

				if (r.originalRecipe.isDynamic())
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

		Map<IRecipeType<?>, Map<ResourceLocation, IRecipe<?>>> newRecipeMap = new HashMap<>();
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
					r.originalRecipe = r.type.serializer.read(r.id, r.json);
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
				r.originalRecipe = r.type.serializer.read(r.id, r.json);
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

	public DynamicMapJS<String, DynamicMapJS<String, RecipeFunction>> getRecipes()
	{
		return recipeFunctions;
	}

	public RecipeJS addRecipe(RecipeJS r, RecipeTypeJS type, ListJS args1)
	{
		addedRecipes.add(r);

		if (r.id == null)
		{
			ResourceLocation itemId = UtilsJS.getMCID(r.outputItems.isEmpty() ? EmptyItemStackJS.INSTANCE.getId() : r.outputItems.get(0).getId());
			r.id = new ResourceLocation(type.serializer.getRegistryName().getNamespace(), "kubejs_generated_" + addedRecipes.size() + "_" + itemId.getNamespace() + "_" + itemId.getPath().replace('/', '_'));
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

	public Predicate<RecipeJS> createFilter(@Nullable Object o)
	{
		if (o == null || o == ALWAYS_TRUE)
		{
			return ALWAYS_TRUE;
		}
		else if (o == ALWAYS_FALSE)
		{
			return ALWAYS_FALSE;
		}

		ListJS list = ListJS.orSelf(o);

		if (list.isEmpty())
		{
			return ALWAYS_TRUE;
		}
		else if (list.size() > 1)
		{
			Predicate<RecipeJS> predicate = ALWAYS_FALSE;

			for (Object o1 : list)
			{
				Predicate<RecipeJS> p = createFilter(o1);

				if (p == ALWAYS_TRUE)
				{
					return ALWAYS_TRUE;
				}
				else if (p != ALWAYS_FALSE)
				{
					predicate = predicate.or(p);
				}
			}

			return predicate;
		}

		MapJS map = MapJS.of(list.get(0));

		if (map == null || map.isEmpty())
		{
			return ALWAYS_TRUE;
		}

		boolean exact = Boolean.TRUE.equals(map.get("exact"));

		Predicate<RecipeJS> predicate = ALWAYS_TRUE;

		if (map.get("or") != null)
		{
			predicate = predicate.and(createFilter(map.get("or")));
		}

		if (map.get("id") != null)
		{
			ResourceLocation id = UtilsJS.getMCID(map.get("id").toString());
			predicate = predicate.and(recipe -> recipe.id.equals(id));
		}

		if (map.get("type") != null)
		{
			ResourceLocation type = UtilsJS.getMCID(map.get("type").toString());
			predicate = predicate.and(recipe -> type.equals(recipe.type.serializer.getRegistryName()));
		}

		if (map.get("group") != null)
		{
			String group = map.get("group").toString();
			predicate = predicate.and(recipe -> recipe.getGroup().equals(group));
		}

		if (map.get("mod") != null)
		{
			String mod = map.get("mod").toString();
			predicate = predicate.and(recipe -> recipe.id.getNamespace().equals(mod));
		}

		if (map.get("input") != null)
		{
			IngredientJS in = IngredientJS.of(map.get("input"));
			predicate = predicate.and(recipe -> recipe.hasInput(in, exact));
		}

		if (map.get("output") != null)
		{
			IngredientJS out = IngredientJS.of(map.get("output"));
			predicate = predicate.and(recipe -> recipe.hasOutput(out, exact));
		}

		return predicate;
	}

	public Predicate<RecipeJS> customFilter(Predicate<RecipeJS> filter)
	{
		return filter;
	}

	public void forEachRecipe(@Nullable Object o, Consumer<RecipeJS> consumer)
	{
		Predicate<RecipeJS> filter = createFilter(o);

		if (filter == ALWAYS_TRUE)
		{
			originalRecipes.forEach(consumer);
		}
		else if (filter != ALWAYS_FALSE)
		{
			originalRecipes.stream().filter(filter).forEach(consumer);
		}
	}

	public int remove(Object filter)
	{
		int[] count = new int[1];
		forEachRecipe(filter, r -> {
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
		forEachRecipe(filter, r -> {
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
		return replaceInput(ALWAYS_TRUE, ingredient, with);
	}

	public int replaceOutput(Object filter, Object ingredient, Object with, boolean exact)
	{
		int[] count = new int[1];
		IngredientJS i = IngredientJS.of(ingredient);
		ItemStackJS w = ItemStackJS.of(with);
		String is = i.toString();
		String ws = w.toString();
		forEachRecipe(filter, r -> {
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
		return replaceOutput(ALWAYS_TRUE, ingredient, with);
	}

	public RecipeFunction getShaped()
	{
		return functionMap.get(IRecipeSerializer.CRAFTING_SHAPED.getRegistryName());
	}

	public RecipeFunction getShapeless()
	{
		return functionMap.get(IRecipeSerializer.CRAFTING_SHAPELESS.getRegistryName());
	}

	public RecipeFunction getSmelting()
	{
		return functionMap.get(IRecipeSerializer.SMELTING.getRegistryName());
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
}