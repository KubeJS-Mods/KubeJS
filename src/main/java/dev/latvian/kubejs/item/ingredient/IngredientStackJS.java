package dev.latvian.kubejs.item.ingredient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.item.ItemStackJS;
import net.minecraft.item.ItemStack;

import java.util.Set;

/**
 * @author LatvianModder
 */
public class IngredientStackJS implements IngredientJS
{
	public IngredientJS ingredient;
	private int countOverride;
	public String countKey;

	public IngredientStackJS(IngredientJS i, int a)
	{
		ingredient = i;
		countOverride = a;
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
	public IngredientJS count(int count)
	{
		if (count == 1)
		{
			return ingredient;
		}

		countOverride = count;
		return this;
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
	public boolean isEmpty()
	{
		return ingredient.isEmpty();
	}

	@Override
	public Set<ItemStackJS> getStacks()
	{
		return ingredient.getStacks();
	}

	@Override
	public IngredientJS not()
	{
		return new IngredientStackJS(ingredient.not(), countOverride);
	}

	@Override
	public ItemStackJS getFirst()
	{
		return ingredient.getFirst().count(getCount());
	}

	@Override
	public String toString()
	{
		return getCount() == 1 ? ingredient.toString() : (getCount() + "x " + ingredient);
	}

	@Override
	public JsonElement toJson()
	{
		if (countOverride > 1)
		{
			JsonObject json = new JsonObject();
			json.add("ingredient", ingredient.toJson());
			json.addProperty(countKey, countOverride);
			return json;
		}

		return ingredient.toJson();
	}
}