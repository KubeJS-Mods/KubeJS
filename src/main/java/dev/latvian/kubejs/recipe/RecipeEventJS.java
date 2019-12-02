package dev.latvian.kubejs.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.latvian.kubejs.documentation.Ignore;
import dev.latvian.kubejs.documentation.P;
import dev.latvian.kubejs.documentation.T;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.recipe.type.CustomRecipeJS;
import dev.latvian.kubejs.recipe.type.RecipeJS;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.script.data.VirtualKubeJSDataPack;
import dev.latvian.kubejs.server.ServerEventJS;
import dev.latvian.kubejs.server.ServerJS;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

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
	private final IResourceManager resourceManager;
	private final List<RecipeJS> recipes;
	private final Map<ResourceLocation, RecipeFunction> deserializerMap;
	private final Map<String, Map<String, RecipeFunction>> recipeFunctions;
	private final Set<ResourceLocation> removedRecipes;
	private final Set<ResourceLocation> brokenRecipes;
	public boolean removeBrokenRecipes;

	private List<RecipeJS> originalRecipes;

	public RecipeEventJS(IResourceManager m, List<RecipeJS> r, Map<ResourceLocation, RecipeFunction> rf)
	{
		resourceManager = m;
		recipes = r;
		deserializerMap = rf;
		recipeFunctions = new HashMap<String, Map<String, RecipeFunction>>()
		{
			@Override
			public Map<String, RecipeFunction> get(Object namespace)
			{
				Map<String, RecipeFunction> map = super.get(namespace);

				if (map == null)
				{
					map = new HashMap<String, RecipeFunction>()
					{
						@Override
						public RecipeFunction get(Object type)
						{
							RecipeFunction function = super.get(type);

							if (function == null)
							{
								ResourceLocation id = new ResourceLocation(namespace.toString(), type.toString());

								function = deserializerMap.get(id);

								if (function == null)
								{
									function = new RecipeFunction(new CustomRecipeJS.CustomType(id), recipes);
								}

								put(type.toString(), function);
							}

							return function;
						}

						@Override
						public boolean containsKey(Object key)
						{
							return true;
						}
					};

					put(namespace.toString(), map);
				}

				return map;
			}

			@Override
			public boolean containsKey(Object key)
			{
				return true;
			}
		};

		removedRecipes = new HashSet<>();
		brokenRecipes = new HashSet<>();
		removeBrokenRecipes = false;
	}

	public Map<String, Map<String, RecipeFunction>> getRecipes()
	{
		return recipeFunctions;
	}

	private List<RecipeJS> getOriginalRecipes()
	{
		if (originalRecipes == null)
		{
			originalRecipes = new ArrayList<>();

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
								serializer.read(recipeId, json);
								RecipeFunction function = getRecipes().get(type.getNamespace()).get(type.getPath());
								r = function.type.create(json);
							}
							catch (Exception ex)
							{
								r = new RecipeErrorJS(ex.toString());
							}
						}

						r.id = recipeId;

						if (!(r instanceof RecipeErrorJS))
						{
							originalRecipes.add(r);
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
					}
				}
			}
			catch (Exception ex)
			{
			}
		}

		return originalRecipes;
	}

	public void nuke()
	{
		remove(recipe -> true);
	}

	public void remove(Predicate<RecipeJS> recipePredicate)
	{
		for (RecipeJS recipe : new ArrayList<>(getOriginalRecipes()))
		{
			if (recipePredicate.test(recipe))
			{
				if (removedRecipes.add(recipe.id))
				{
					originalRecipes.remove(recipe);

					if (ServerJS.instance.logRemovedRecipes)
					{
						ScriptType.SERVER.console.info("Removed recipe " + recipe.id);
					}
				}
			}
		}
	}

	public void removeId(@P("id") @T(ResourceLocation.class) Object id)
	{
		ResourceLocation location = UtilsJS.getID(id);
		remove(recipe -> recipe.id.equals(location));
	}

	public void removeType(@P("type") @T(ResourceLocation.class) Object type)
	{
		ResourceLocation location = UtilsJS.getID(type);
		remove(recipe -> recipe.getType().id.equals(location));
	}

	public void removeGroup(@P("group") String group)
	{
		remove(recipe -> recipe.group.equals(group));
	}

	public void removeMod(@P("mod") String mod)
	{
		remove(recipe -> recipe.id.getNamespace().equals(mod));
	}

	public void removeInput(@P("ingredient") @T(IngredientJS.class) Object ingredient)
	{
		IngredientJS in = IngredientJS.of(ingredient);
		remove(recipe -> recipe.hasInput(in));
	}

	public void removeOutput(@P("ingredient") @T(IngredientJS.class) Object ingredient)
	{
		IngredientJS in = IngredientJS.of(ingredient);
		remove(recipe -> recipe.hasOutput(in));
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

		for (RecipeJS r : recipes)
		{
			r.addToDataPack(pack);
		}

		ScriptType.SERVER.console.info("Added " + recipes.size() + " recipes, removed " + removedRecipes.size() + " recipes, removed " + brokenRecipes.size() + " broken recipes");
	}
}