package dev.latvian.kubejs.recipe;

import com.google.gson.JsonObject;
import dev.latvian.kubejs.KubeJSCore;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.recipe.type.RecipeJS;
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

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * @author LatvianModder
 */
public class RecipeEventJS extends ServerEventJS
{
	private static final Predicate<RecipeJS> ALWAYS_TRUE = r -> true;
	private static final Predicate<RecipeJS> ALWAYS_FALSE = r -> false;

	private final Map<ResourceLocation, RecipeTypeJS> typeMap;
	private final RecipeCollection originalRecipes;

	private final List<RecipeJS> addedRecipes;
	public final Map<ResourceLocation, RecipeFunction> functionMap;

	private final DynamicMapJS<DynamicMapJS<RecipeFunction>> recipeFunctions;

	public RecipeEventJS(RecipeManager recipeManager, Map<ResourceLocation, RecipeTypeJS> t, Map<ResourceLocation, JsonObject> jsonMap)
	{
		typeMap = t;
		originalRecipes = new RecipeCollection(new ArrayList<>());

		ScriptType.SERVER.console.logger.info("Scanning recipes...");

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
				r.json = jsonMap.get(r.id);

				if (r.json.has("recipes") && r.json.has("type") && r.json.get("type").getAsString().equals("forge:conditional"))
				{
					r.json = r.json.get("recipes").getAsJsonArray().get(0).getAsJsonObject().get("recipe").getAsJsonObject();
				}

				try
				{
					r.deserialize();
					originalRecipes.list.add(r);
					ScriptType.SERVER.console.logger.debug("* " + r + ": " + r.getInput() + " -> " + r.getOutput());
				}
				catch (Exception ex)
				{
					ScriptType.SERVER.console.logger.warn("! " + r + ": " + ex);
				}
			}
		}

		addedRecipes = new ArrayList<>();
		functionMap = new HashMap<>();

		for (RecipeTypeJS type : typeMap.values())
		{
			functionMap.put(type.serializer.getRegistryName(), new RecipeFunction(this, type.serializer.getRegistryName(), type));
		}

		recipeFunctions = new DynamicMapJS<>(n -> new DynamicMapJS<>(p -> functionMap.computeIfAbsent(new ResourceLocation(n, p), id -> new RecipeFunction(this, id, null))));
	}

	public void post(RecipeManager recipeManager)
	{
		ScriptType.SERVER.console.logger.info("Found " + originalRecipes.list.size() + " recipes");
		ScriptType.SERVER.console.setLineNumber(true);
		post(ScriptType.SERVER, KubeJSEvents.RECIPES);
		post(ScriptType.SERVER, "server.datapack.recipes"); // TODO: To be removed some time later
		ScriptType.SERVER.console.setLineNumber(false);

		Map<IRecipeType<?>, Map<ResourceLocation, IRecipe<?>>> newRecipeMap = new HashMap<>();
		int removed = 0;
		int modified = 0;
		int added = 0;

		for (RecipeJS r : originalRecipes.list)
		{
			if (r.remove)
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

	public DynamicMapJS<DynamicMapJS<RecipeFunction>> getRecipes()
	{
		return recipeFunctions;
	}

	public RecipeJS addRecipe(RecipeJS recipe, RecipeTypeJS type, ListJS args1)
	{
		addedRecipes.add(recipe);

		if (recipe.id == null)
		{
			recipe.id = new ResourceLocation(type.serializer.getRegistryName().getNamespace(), "kubejs_generated_" + addedRecipes.size());
		}

		String s = "+ " + recipe + ": " + recipe.getInput() + " -> " + recipe.getOutput();

		if (ServerJS.instance.logAddedRecipes)
		{
			ScriptType.SERVER.console.logger.info(s);
		}
		else
		{
			ScriptType.SERVER.console.logger.debug(s);
		}

		return recipe;
	}

	public RecipeCollection getAll()
	{
		return originalRecipes;
	}

	public Predicate<RecipeJS> createPredicate(@Nullable Object o)
	{
		if (o == null)
		{
			return ALWAYS_TRUE;
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
				Predicate<RecipeJS> p = createPredicate(o1);

				if (p == ALWAYS_TRUE)
				{
					return ALWAYS_TRUE;
				}

				predicate = predicate.or(p);
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
			predicate = predicate.and(createPredicate(map.get("or")));
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

	public RecipeCollection get(@Nullable Object o)
	{
		return originalRecipes.filter(createPredicate(o));
	}

	public void remove(Object filter)
	{
		get(filter).remove();
	}

	public void replaceInput(Object filter, Object ingredient, Object with)
	{
		get(filter).replaceInput(ingredient, with);
	}

	public void replaceOutput(Object filter, Object ingredient, Object with)
	{
		get(filter).replaceOutput(ingredient, with);
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