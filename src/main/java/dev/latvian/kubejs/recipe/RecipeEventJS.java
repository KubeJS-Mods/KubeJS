package dev.latvian.kubejs.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.latvian.kubejs.documentation.Ignore;
import dev.latvian.kubejs.documentation.P;
import dev.latvian.kubejs.documentation.T;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.recipe.type.CustomRecipeJS;
import dev.latvian.kubejs.recipe.type.RecipeJS;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.script.data.VirtualKubeJSDataPack;
import dev.latvian.kubejs.server.ServerEventJS;
import dev.latvian.kubejs.server.ServerJS;
import dev.latvian.kubejs.util.DynamicMapJS;
import dev.latvian.kubejs.util.ListJS;
import dev.latvian.kubejs.util.MapJS;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

/**
 * @author LatvianModder
 */
public class RecipeEventJS extends ServerEventJS
{
	private static final Predicate<RecipeJS> ALWAYS_TRUE = r -> true;
	private static final Predicate<RecipeJS> ALWAYS_FALSE = r -> false;

	@Ignore
	private final List<RecipeJS> addedRecipes;
	@Ignore
	public final Map<ResourceLocation, RecipeFunction> deserializerMap;

	private final DynamicMapJS<DynamicMapJS<RecipeFunction>> recipeFunctions;
	private final Set<ResourceLocation> removedRecipes;
	private final Set<ResourceLocation> brokenRecipes;
	public boolean removeBrokenRecipes;
	private RecipeCollection originalRecipes;

	public RecipeEventJS()
	{
		addedRecipes = new ArrayList<>();
		deserializerMap = new HashMap<>();
		recipeFunctions = new DynamicMapJS<>(n -> new DynamicMapJS<>(p -> deserializerMap.computeIfAbsent(new ResourceLocation(n, p), id -> new RecipeFunction(this, new CustomRecipeJS.CustomType(id)))));
		removedRecipes = new HashSet<>();
		brokenRecipes = new HashSet<>();
		removeBrokenRecipes = false;
		originalRecipes = new RecipeCollection(new ArrayList<>());

		originalRecipes.recipeChanged = recipe -> {
			if (removedRecipes.add(recipe.id))
			{
				addedRecipes.add(recipe);
				ScriptType.SERVER.console.info("Changed recipe " + recipe.id);
			}
		};
	}

	@Ignore
	public void loadRecipes(IResourceManager resourceManager)
	{
		try
		{
			for (ResourceLocation location : resourceManager.getAllResourceLocations("recipes", s -> s.endsWith(".json")))
			{
				try (InputStreamReader reader = new InputStreamReader(resourceManager.getResource(location).getInputStream()))
				{
					JsonObject json = new JsonParser().parse(reader).getAsJsonObject();
					ResourceLocation type = new ResourceLocation(json.get("type").getAsString());
					ResourceLocation recipeId = new ResourceLocation(location.getNamespace(), location.getPath().substring(8, location.getPath().length() - 5));

					IRecipeSerializer serializer = ForgeRegistries.RECIPE_SERIALIZERS.getValue(type);
					RecipeJS r;

					if (serializer == null)
					{
						r = new RecipeErrorJS("Recipe type '" + type + "' is not registered!");
					}
					else
					{
						try
						{
							if (removeBrokenRecipes)
							{
								serializer.read(recipeId, json);
							}

							RecipeFunction function = getRecipes().get(type.getNamespace()).get(type.getPath());
							r = function.type.create(json);
						}
						catch (Exception ex)
						{
							r = new RecipeErrorJS(ex.toString());
						}
					}

					r.event = this;
					r.id = recipeId;

					if (!(r instanceof RecipeErrorJS))
					{
						originalRecipes.list.add(r);
						r.group = json.has("group") ? json.get("group").getAsString() : "";
					}
					else if (removeBrokenRecipes)
					{
						brokenRecipes.add(r.id);

						if (ServerJS.instance.logRemovedRecipes)
						{
							ScriptType.SERVER.console.info("Removed broken recipe " + r.id + ": " + ((RecipeErrorJS) r).message);
						}
					}
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		originalRecipes.list.sort(null);
	}

	public DynamicMapJS<DynamicMapJS<RecipeFunction>> getRecipes()
	{
		return recipeFunctions;
	}

	@Ignore
	public RecipeJS addRecipe(RecipeJS recipe, RecipeTypeJS type, ListJS args1)
	{
		if (recipe instanceof RecipeErrorJS)
		{
			ScriptType.SERVER.console.error("Broken '" + type.id + "' recipe: " + ((RecipeErrorJS) recipe).message);
			ScriptType.SERVER.console.error(args1);
			ScriptType.SERVER.console.error("");
			return recipe;
		}

		recipe.event = this;

		if (recipe.id == null)
		{
			recipe.id = new ResourceLocation(type.id.getNamespace(), "kubejs_generated_" + addedRecipes.size());
		}

		if (recipe.group == null)
		{
			recipe.group = "";
		}

		if (ServerJS.instance.logAddedRecipes)
		{
			ScriptType.SERVER.console.info("Added '" + type.id + "' recipe: " + recipe.toJson());
		}

		addedRecipes.add(recipe);
		return recipe;
	}

	@Ignore
	public void removeRecipe(RecipeJS recipe)
	{
		if (removedRecipes.add(recipe.id))
		{
			if (ServerJS.instance.logRemovedRecipes)
			{
				ScriptType.SERVER.console.info("Removed recipe " + recipe.id);
			}
		}
	}

	public RecipeCollection getAll()
	{
		return originalRecipes;
	}

	public static Predicate<RecipeJS> createPredicate(@Nullable Object o)
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
			ResourceLocation type = UtilsJS.getID(map.get("type"));
			predicate = predicate.and(recipe -> recipe.getType().id.equals(type));
		}

		if (map.get("group") != null)
		{
			String group = map.get("group").toString();
			predicate = predicate.and(recipe -> recipe.group.equals(group));
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

	public RecipeCollection get(@Nullable @P("filter") @T(MapJS.class) Object o)
	{
		return originalRecipes.filter(createPredicate(o));
	}

	public void remove(@P("filter") @T(MapJS.class) Object filter)
	{
		get(filter).remove();
	}

	public void replaceInput(@P("filter") @T(MapJS.class) Object filter, @P("ingredient") @T(IngredientJS.class) Object ingredient, @P("with") @T(IngredientJS.class) Object with)
	{
		get(filter).replaceInput(ingredient, with);
	}

	public void replaceOutput(@P("filter") @T(MapJS.class) Object filter, @P("ingredient") @T(IngredientJS.class) Object ingredient, @P("with") @T(ItemStackJS.class) Object with)
	{
		get(filter).replaceOutput(ingredient, with);
	}

	public RecipeFunction getShaped()
	{
		return deserializerMap.get(IRecipeSerializer.CRAFTING_SHAPED.getRegistryName());
	}

	public RecipeFunction getShapeless()
	{
		return deserializerMap.get(IRecipeSerializer.CRAFTING_SHAPELESS.getRegistryName());
	}

	public RecipeFunction getSmelting()
	{
		return deserializerMap.get(IRecipeSerializer.SMELTING.getRegistryName());
	}

	@Ignore
	public void addDataToPack(VirtualKubeJSDataPack pack)
	{
		for (ResourceLocation r : removedRecipes)
		{
			pack.addData(new ResourceLocation(r.getNamespace(), "recipes/" + r.getPath() + ".json"), "{\"type\":\"kubejs:deleted\"}");
		}

		for (ResourceLocation r : brokenRecipes)
		{
			pack.addData(new ResourceLocation(r.getNamespace(), "recipes/" + r.getPath() + ".json"), "{\"type\":\"kubejs:deleted\"}");
		}

		for (RecipeJS r : addedRecipes)
		{
			r.addToDataPack(pack);
		}

		ScriptType.SERVER.console.info("Added " + addedRecipes.size() + " recipes, removed " + removedRecipes.size() + " recipes, removed " + brokenRecipes.size() + " broken recipes");
	}
}