package dev.latvian.kubejs.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.latvian.kubejs.KubeJSCore;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.item.EmptyItemStackJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.server.ServerEventJS;
import dev.latvian.kubejs.server.ServerJS;
import dev.latvian.kubejs.util.DynamicMapJS;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author LatvianModder
 */
public class RecipeEventJS extends ServerEventJS
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

		ScriptType.SERVER.console.logger.info("Scanning recipes...");

		/*
		for (Map.Entry<IRecipeType<?>, Map<ResourceLocation, IRecipe<?>>> entry : KubeJSCore.getRecipes(recipeManager).entrySet())
		{
			ScriptType.SERVER.console.logger.debug(entry.getKey().toString());

			for (IRecipe<?> recipe : entry.getValue().values())
			{
				IRecipeSerializer serializer = recipe.getSerializer();
				RecipeTypeJS type = typeMap.computeIfAbsent(serializer.getRegistryName(), id -> new CustomRecipeTypeJS(serializer));
				RecipeJS r = type.factory.get();
				r.id = recipe.getId();
				r.type = type;
				r.originalRecipe = recipe;
				r.json = JsonUtilsJS.copy(jsonMap.get(r.id)).getAsJsonObject();

				if (r.json.has("recipes") && r.json.has("type") && r.json.get("type").getAsString().equals("forge:conditional"))
				{
					r.json = r.json.get("recipes").getAsJsonArray().get(0).getAsJsonObject().get("recipe").getAsJsonObject();
				}

				try
				{
					r.deserialize();
					originalRecipes.add(r);
					ScriptType.SERVER.console.logger.debug("* " + r + ": " + r.getInput() + " -> " + r.getOutput());
				}
				catch (Exception ex)
				{
					ScriptType.SERVER.console.logger.warn("! " + r + ": " + ex);
				}
			}
		}
		 */

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
					ScriptType.SERVER.console.logger.info("Skipping loading recipe {} as it's conditions were not met", recipeId);
					continue;
				}

				JsonElement t = json.get("type");

				if (!(t instanceof JsonPrimitive) || !((JsonPrimitive) t).isString())
				{
					ScriptType.SERVER.console.logger.warn("Missing or invalid recipe recipe type, expected a string in recipe {}", recipeId);
					continue;
				}

				RecipeFunction function = functionMap.get(new ResourceLocation(t.getAsString()));

				if (function.type == null)
				{
					ScriptType.SERVER.console.logger.warn("Skipping loading recipe {} as it's type {} is unknown", recipeId, function.typeID);
					continue;
				}

				RecipeJS recipeJS = function.type.factory.get();
				recipeJS.id = recipeId;
				recipeJS.type = function.type;
				recipeJS.json = json;
				recipeJS.originalRecipe = function.type.serializer.read(recipeId, json);

				if (recipeJS.originalRecipe == null)
				{
					ScriptType.SERVER.console.logger.warn("Skipping loading recipe {} as it's serializer returned null", recipeId);
					continue;
				}

				recipeJS.deserialize();
				originalRecipes.add(recipeJS);
				ScriptType.SERVER.console.logger.debug("Loaded recipe {}: {} -> {}", recipeId, recipeJS.inputItems, recipeJS.outputItems);
			}
			catch (Exception ex)
			{
				ScriptType.SERVER.console.logger.error("Parsing error loading recipe {}: {}", recipeId, ex);
			}
		}

		ScriptType.SERVER.console.logger.info("Found " + originalRecipes.size() + " recipes");
		ScriptType.SERVER.console.setLineNumber(true);
		post(ScriptType.SERVER, KubeJSEvents.RECIPES);
		post(ScriptType.SERVER, "server.datapack.recipes"); // TODO: To be removed some time later
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

		KubeJSCore.setRecipes(recipeManager, newRecipeMap);
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
			ResourceLocation itemId = r.outputItems.isEmpty() ? EmptyItemStackJS.INSTANCE.getId() : r.outputItems.get(0).getId();
			r.id = new ResourceLocation(type.serializer.getRegistryName().getNamespace(), "kubejs_generated_" + addedRecipes.size() + "_" + itemId.getNamespace() + "_" + itemId.getPath().replace('/', '_'));
		}

		if (ServerJS.instance.logAddedRecipes)
		{
			ScriptType.SERVER.console.logger.info("+ {}: {} -> {}", r, r.inputItems, r.outputItems);
		}
		else
		{
			ScriptType.SERVER.console.logger.debug("+ {}: {} -> {}", r, r.inputItems, r.outputItems);
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

		Predicate<RecipeJS> predicate = ALWAYS_TRUE;

		if (map.get("or") != null)
		{
			predicate = predicate.and(createFilter(map.get("or")));
		}

		if (map.get("id") != null)
		{
			ResourceLocation id = UtilsJS.getID(map.get("id"));
			predicate = predicate.and(recipe -> recipe.id.equals(id));
		}

		if (map.get("type") != null)
		{
			RecipeTypeJS type = typeMap.get(UtilsJS.getID(map.get("type")));

			if (type == null)
			{
				return ALWAYS_FALSE;
			}

			predicate = predicate.and(recipe -> recipe.type.equals(type));
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
			predicate = predicate.and(recipe -> recipe.hasInput(in));
		}

		if (map.get("output") != null)
		{
			IngredientJS out = IngredientJS.of(map.get("output"));
			predicate = predicate.and(recipe -> recipe.hasOutput(out));
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
				if (ServerJS.instance.logRemovedRecipes)
				{
					ScriptType.SERVER.console.logger.info("- {}: {} -> {}", r, r.inputItems, r.outputItems);
				}
				else
				{
					ScriptType.SERVER.console.logger.debug("- {}: {} -> {}", r, r.inputItems, r.outputItems);
				}

				count[0]++;
			}
		});
		return count[0];
	}

	public int replaceInput(Object filter, Object ingredient, Object with)
	{
		String is = ingredient.toString();
		String ws = with.toString();
		int[] count = new int[1];
		IngredientJS i = IngredientJS.of(ingredient);
		IngredientJS w = IngredientJS.of(with);
		forEachRecipe(filter, r -> {
			if (r.replaceInput(i, w))
			{
				count[0]++;
				ScriptType.SERVER.console.logger.info("~ {}: OUT {} -> {}", r, is, ws);
			}
		});
		return count[0];
	}

	public int replaceInput(Object ingredient, Object with)
	{
		return replaceInput(ALWAYS_TRUE, ingredient, with);
	}

	public int replaceOutput(Object filter, Object ingredient, Object with)
	{
		String is = ingredient.toString();
		String ws = with.toString();
		int[] count = new int[1];
		IngredientJS i = IngredientJS.of(ingredient);
		ItemStackJS w = ItemStackJS.of(with);
		forEachRecipe(filter, r -> {
			if (r.replaceOutput(i, w))
			{
				count[0]++;
				ScriptType.SERVER.console.logger.info("~ {}: IN {} -> {}", r, is, ws);
			}
		});
		return count[0];
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
}