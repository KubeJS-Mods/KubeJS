package dev.latvian.kubejs.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.latvian.kubejs.documentation.P;
import dev.latvian.kubejs.documentation.T;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.recipe.type.CustomRecipeJS;
import dev.latvian.kubejs.recipe.type.RecipeJS;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.server.ServerEventJS;
import dev.latvian.kubejs.server.ServerJS;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.InputStreamReader;
import java.util.ArrayList;
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
	private final Map<String, RecipeFunction> recipes;
	private final Map<IRecipeSerializer, RecipeDeserializerJS> deserializerMap;
	private final Set<ResourceLocation> deletedRecipes;

	private List<RecipeJS> originalRecipes;

	public RecipeEventJS(IResourceManager m, Map<String, RecipeFunction> r, Set<ResourceLocation> d, Map<IRecipeSerializer, RecipeDeserializerJS> dm)
	{
		resourceManager = m;
		recipes = r;
		deletedRecipes = d;
		deserializerMap = dm;
	}

	public Map<String, RecipeFunction> getRecipes()
	{
		return recipes;
	}

	private RecipeJS parse(CustomRecipeJS custom)
	{
		try
		{
			RecipeJS r = deserializerMap.get(custom.type).create(custom.data);

			if (r != null)
			{
				return r;
			}
		}
		catch (Exception ex)
		{
		}

		return custom;
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
						CustomRecipeJS custom = (CustomRecipeJS) CustomRecipeJS.DESERIALIZER.create(json);

						if (custom != null)
						{
							RecipeJS r = parse(custom);
							originalRecipes.add(r);
							r.id = new ResourceLocation(location.getNamespace(), location.getPath().substring(8, location.getPath().length() - 5));
							r.group = json.has("group") ? json.get("group").getAsString() : "";
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
		delete(recipe -> true);
	}

	public void delete(Predicate<RecipeJS> recipePredicate)
	{
		for (RecipeJS recipe : new ArrayList<>(getOriginalRecipes()))
		{
			if (recipePredicate.test(recipe))
			{
				if (deletedRecipes.add(recipe.id))
				{
					originalRecipes.remove(recipe);

					if (ServerJS.instance.debugLog)
					{
						ScriptType.SERVER.console.info("Deleted recipe " + recipe.id);
					}
				}
			}
		}
	}

	public void deleteId(@P("id") @T(ResourceLocation.class) Object id)
	{
		ResourceLocation location = UtilsJS.getID(id);
		delete(recipe -> recipe.id.equals(location));
	}

	public void deleteType(@P("type") @T(ResourceLocation.class) Object type)
	{
		IRecipeSerializer serializer = ForgeRegistries.RECIPE_SERIALIZERS.getValue(UtilsJS.getID(type));
		delete(recipe -> recipe.getSerializer() == serializer);
	}

	public void deleteGroup(@P("group") String group)
	{
		delete(recipe -> recipe.group.equals(group));
	}

	public void deleteMod(@P("mod") String mod)
	{
		delete(recipe -> recipe.id.getNamespace().equals(mod));
	}

	public void deleteInput(@P("ingredient") @T(IngredientJS.class) Object ingredient)
	{
		IngredientJS in = IngredientJS.of(ingredient);
		delete(recipe -> recipe.hasInput(in));
	}

	public void deleteOutput(@P("ingredient") @T(IngredientJS.class) Object ingredient)
	{
		IngredientJS in = IngredientJS.of(ingredient);
		delete(recipe -> recipe.hasOutput(in));
	}
}