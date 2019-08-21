package dev.latvian.kubejs.crafting.handlers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.util.JsonUtilsJS;
import dev.latvian.kubejs.util.RegistryEventJS;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * @author LatvianModder
 */
public class CraftingTableRecipeEventJS extends RegistryEventJS<IRecipe>
{
	public CraftingTableRecipeEventJS(IForgeRegistry<IRecipe> r)
	{
		super(r);
	}

	public void addShaped(String recipeID, String output, String[] pattern, Map<String, Object> ingredients)
	{
		String id = KubeJS.ID_CONTEXT.appendModId(recipeID);
		ItemStack outputItem = UtilsJS.INSTANCE.item(output).itemStack();

		if (outputItem.isEmpty())
		{
			KubeJS.LOGGER.warn("Recipe " + id + " has broken output!");
			return;
		}

		int w = pattern[0].length();
		int h = pattern.length;

		NonNullList<Ingredient> ingredientList = NonNullList.withSize(w * h, Ingredient.EMPTY);
		Map<Character, Ingredient> ingredientMap = new HashMap<>();

		for (Map.Entry<String, Object> entry : ingredients.entrySet())
		{
			Ingredient i = UtilsJS.INSTANCE.ingredient(entry.getValue()).createVanillaIngredient();

			if (i != Ingredient.EMPTY && !entry.getKey().isEmpty())
			{
				ingredientMap.put(entry.getKey().charAt(0), i);
			}
		}

		boolean errored = true;

		for (int i = 0; i < w * h; i++)
		{
			Ingredient in = ingredientMap.get(pattern[i / w].charAt(i % w));

			if (in != null)
			{
				ingredientList.set(i, in);
				errored = false;
			}
		}

		if (errored)
		{
			KubeJS.LOGGER.warn("Recipe " + id + " has broken items! Check the pattern/ingredient list.");
		}
		else
		{
			ShapedRecipes r = new ShapedRecipes(id, w, h, ingredientList, outputItem);
			r.setRegistryName(new ResourceLocation(id));
			registry.register(r);
		}
	}

	public void addShaped(String output, String[] pattern, Map<String, Object> ingredients)
	{
		addShaped(output, output, pattern, ingredients);
	}

	public void addShapeless(String recipeID, String output, Object[] ingredients)
	{
		String id = KubeJS.ID_CONTEXT.appendModId(recipeID);
		ItemStack outputItem = UtilsJS.INSTANCE.item(output).itemStack();

		if (outputItem.isEmpty())
		{
			KubeJS.LOGGER.warn("Recipe " + id + " has broken output!");
			return;
		}

		NonNullList<Ingredient> ingredientList = NonNullList.create();

		for (Object ingredient : ingredients)
		{
			Ingredient i = UtilsJS.INSTANCE.ingredient(ingredient).createVanillaIngredient();

			if (i != Ingredient.EMPTY)
			{
				ingredientList.add(i);
			}
		}

		if (ingredientList.isEmpty())
		{
			KubeJS.LOGGER.warn("Recipe " + id + " has broken items! Check the ingredient list.");
		}
		else
		{
			ShapelessRecipes r = new ShapelessRecipes(id, outputItem, ingredientList);
			r.setRegistryName(new ResourceLocation(id));
			registry.register(r);
		}
	}

	public void addShapeless(String output, Object[] ingredients)
	{
		addShapeless(output, output, ingredients);
	}

	public void add(String recipeID, Object recipe)
	{
		JsonElement e = JsonUtilsJS.INSTANCE.from(recipe);

		if (!e.isJsonObject())
		{
			return;
		}

		JsonObject o = e.getAsJsonObject();

		if (!o.has("type"))
		{
			return;
		}

		String type = o.get("type").getAsString();

		if (type.equals("minecraft:crafting_shaped") && o.has("key"))
		{
			JsonObject object = o.get("key").getAsJsonObject();

			for (Map.Entry<String, JsonElement> entry : object.entrySet())
			{
				object.add(entry.getKey(), fixIngredient(entry.getValue()));
			}
		}
		else if (type.equals("minecraft:crafting_shapeless") && o.has("ingredients"))
		{
			JsonArray array = o.get("ingredients").getAsJsonArray();

			for (int i = 0; i < array.size(); i++)
			{
				array.set(i, fixIngredient(array.get(i)));
			}
		}

		try
		{
			IRecipe r = CraftingHelper.getRecipe(o, KubeJS.ID_CONTEXT);

			if (r != null)
			{
				registry.register(setID(recipeID, r));
			}
		}
		catch (Exception ex)
		{
			KubeJS.LOGGER.warn("Failed to load a recipe with id '" + recipeID + "'!");
			ex.printStackTrace();
		}
	}

	private JsonElement fixIngredient(JsonElement element)
	{
		if (element.isJsonPrimitive())
		{
			String s = element.getAsString();

			if (s.startsWith("ore:"))
			{
				JsonObject object = new JsonObject();
				object.addProperty("type", "forge:ore_dict");
				object.addProperty("ore", s.substring(4));
				return object;
			}
			else
			{
				JsonObject object = new JsonObject();
				object.addProperty("item", s);
				return object;
			}
		}

		return element;
	}

	public void removeAdvanced(Predicate<IRecipe> predicate)
	{
		if (registry instanceof ForgeRegistry)
		{
			ForgeRegistry<IRecipe> r = (ForgeRegistry<IRecipe>) registry;
			boolean frozen = r.isLocked();

			if (frozen)
			{
				r.unfreeze();
			}

			List<IRecipe> recipes = new ArrayList<>(r.getValuesCollection());

			for (IRecipe recipe : recipes)
			{
				if (predicate.test(recipe))
				{
					r.remove(recipe.getRegistryName());
				}
			}

			if (frozen)
			{
				r.freeze();
			}
		}
	}

	public void remove(@Nullable Object output)
	{
		Ingredient ingredient = UtilsJS.INSTANCE.ingredient(output).createVanillaIngredient();
		removeAdvanced(recipe -> ingredient.apply(recipe.getRecipeOutput()));
	}

	public void removeID(Object id)
	{
		if (registry instanceof ForgeRegistry)
		{
			ForgeRegistry<IRecipe> r = (ForgeRegistry<IRecipe>) registry;
			boolean frozen = r.isLocked();

			if (frozen)
			{
				r.unfreeze();
			}

			r.remove(UtilsJS.INSTANCE.idMC(UtilsJS.INSTANCE.id(id)));

			if (frozen)
			{
				r.freeze();
			}
		}
	}

	public void removeGroup(Object id)
	{
		ResourceLocation group = UtilsJS.INSTANCE.idMC(UtilsJS.INSTANCE.id(id));
		removeAdvanced(recipe -> new ResourceLocation(recipe.getGroup()).equals(group));
	}

	public void removeMod(String modid)
	{
		removeAdvanced(recipe -> recipe.getRecipeOutput().getItem().getRegistryName().getNamespace().equals(modid));
	}
}