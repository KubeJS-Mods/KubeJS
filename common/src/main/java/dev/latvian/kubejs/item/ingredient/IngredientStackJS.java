package dev.latvian.kubejs.item.ingredient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.item.EmptyItemStackJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.recipe.RecipeJS;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Set;

/**
 * @author LatvianModder
 */
public class IngredientStackJS implements IngredientJS
{
	public static IngredientStackJS stackOf(IngredientJS in)
	{
		return in instanceof IngredientStackJS ? (IngredientStackJS) in : new IngredientStackJS(in, 1);
	}

	public IngredientJS ingredient;
	private final int countOverride;
	public String ingredientKey;
	public String countKey;

	public IngredientStackJS(IngredientJS i, int c)
	{
		ingredient = i;
		countOverride = c;
		ingredientKey = "ingredient";
		countKey = "count";
	}

	public IngredientJS getIngredient()
	{
		return ingredient;
	}

	@Override
	public int getCount()
	{
		return countOverride;
	}

	@Override
	public IngredientJS withCount(int count)
	{
		if (count <= 0)
		{
			return EmptyItemStackJS.INSTANCE;
		}
		else if (count == 1)
		{
			return ingredient;
		}

		return count == countOverride ? this : new IngredientStackJS(ingredient, count);
	}

	@Override
	public IngredientJS getCopy()
	{
		return new IngredientStackJS(ingredient.getCopy(), countOverride);
	}

	@Override
	public boolean test(ItemStackJS stack)
	{
		return ingredient.test(stack);
	}

	@Override
	public boolean testVanilla(ItemStack stack)
	{
		return ingredient.testVanilla(stack);
	}

	@Override
	public boolean testVanillaItem(Item item)
	{
		return ingredient.testVanillaItem(item);
	}

	@Override
	public boolean isEmpty()
	{
		return ingredient.isEmpty();
	}

	@Override
	public boolean isInvalidRecipeIngredient()
	{
		return countOverride <= 0 || ingredient.isInvalidRecipeIngredient();
	}

	@Override
	public Set<ItemStackJS> getStacks()
	{
		return ingredient.getStacks();
	}

	@Override
	public Set<Item> getVanillaItems()
	{
		return ingredient.getVanillaItems();
	}

	@Override
	public IngredientJS not()
	{
		return new IngredientStackJS(ingredient.not(), countOverride);
	}

	@Override
	public IngredientJS filter(IngredientJS filter)
	{
		return new IngredientStackJS(ingredient.filter(filter), countOverride);
	}

	@Override
	public ItemStackJS getFirst()
	{
		return ingredient.getFirst().withCount(getCount());
	}

	@Override
	public String toString()
	{
		return getCount() == 1 ? ingredient.toString() : (getCount() + "x " + ingredient);
	}

	@Override
	public JsonElement toJson()
	{
		if (RecipeJS.currentRecipe != null)
		{
			JsonElement e = RecipeJS.currentRecipe.serializeIngredientStack(this);

			if (e != null)
			{
				return e;
			}
		}

		JsonObject json = new JsonObject();
		json.add(ingredientKey, ingredient.toJson());
		json.addProperty(countKey, countOverride);
		return json;
	}

	@Override
	public IngredientStackJS asIngredientStack()
	{
		return this;
	}
}