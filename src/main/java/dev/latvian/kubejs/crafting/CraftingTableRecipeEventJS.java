package dev.latvian.kubejs.crafting;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.item.ingredient.VanillaIngredientWrapper;
import dev.latvian.kubejs.util.ID;
import dev.latvian.kubejs.util.JsonUtilsJS;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;
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
public class CraftingTableRecipeEventJS extends EventJS
{
	private final IForgeRegistry<IRecipe> registry;

	public CraftingTableRecipeEventJS(IForgeRegistry<IRecipe> r)
	{
		registry = r;
	}

	public void addShaped(String recipeID, Object output, String[] pattern, Map<String, Object> ingredients)
	{
		String id = KubeJS.appendModId(recipeID);
		ItemStack outputItem = ItemStackJS.of(output).getItemStack();

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
			IngredientJS i = IngredientJS.of(entry.getValue());

			if (!i.isEmpty() && !entry.getKey().isEmpty())
			{
				ingredientMap.put(entry.getKey().charAt(0), new VanillaIngredientWrapper(i));
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

	public void addShaped(Object output, String[] pattern, Map<String, Object> ingredients)
	{
		addShaped(output instanceof String ? output.toString() : String.valueOf(output).replaceAll("\\W", "_"), output, pattern, ingredients);
	}

	public void addShapeless(String recipeID, Object output, Object[] ingredients)
	{
		String id = KubeJS.appendModId(recipeID);
		ItemStack outputItem = ItemStackJS.of(output).getItemStack();

		if (outputItem.isEmpty())
		{
			KubeJS.LOGGER.warn("Recipe " + id + " has broken output!");
			return;
		}

		NonNullList<Ingredient> ingredientList = NonNullList.create();

		for (Object ingredient : ingredients)
		{
			IngredientJS i = IngredientJS.of(ingredient);

			if (!i.isEmpty())
			{
				ingredientList.add(new VanillaIngredientWrapper(i));
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

	public void addShapeless(Object output, Object[] ingredients)
	{
		addShapeless(output instanceof String ? output.toString() : String.valueOf(output).replaceAll("\\W", "_"), output, ingredients);
	}

	public void add(String recipeID, Object recipe)
	{
		ID id = ID.of(KubeJS.appendModId(recipeID));
		JsonElement e = JsonUtilsJS.of(recipe);

		if (!e.isJsonObject())
		{
			KubeJS.LOGGER.warn("Recipe " + id + " is not an object!");
			return;
		}

		JsonObject o = e.getAsJsonObject();

		if (!o.has("type"))
		{
			KubeJS.LOGGER.warn("Recipe " + id + " doesn't have type!");
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
			IRecipe r = CraftingHelper.getRecipe(o, new JsonContext(KubeJS.MOD_ID));

			if (r != null)
			{
				registry.register(r.setRegistryName(id.mc()));
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
		IngredientJS ingredient = IngredientJS.of(output);
		removeAdvanced(recipe -> ingredient.test(recipe.getRecipeOutput()));
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

			r.remove(ID.of(id).mc());

			if (frozen)
			{
				r.freeze();
			}
		}
	}

	public void removeGroup(Object id)
	{
		ResourceLocation group = ID.of(id).mc();
		removeAdvanced(recipe -> new ResourceLocation(recipe.getGroup()).equals(group));
	}

	public void removeMod(String modid)
	{
		removeAdvanced(recipe -> recipe.getRecipeOutput().getItem().getRegistryName().getNamespace().equals(modid));
	}
}