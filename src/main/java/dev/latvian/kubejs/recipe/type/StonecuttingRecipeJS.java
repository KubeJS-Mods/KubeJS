package dev.latvian.kubejs.recipe.type;

import dev.latvian.kubejs.item.EmptyItemStackJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.kubejs.util.ListJS;

import java.util.Collection;
import java.util.Collections;

/**
 * @author LatvianModder
 */
public class StonecuttingRecipeJS extends RecipeJS
{
	private IngredientJS ingredient = EmptyItemStackJS.INSTANCE;
	private ItemStackJS result = EmptyItemStackJS.INSTANCE;

	@Override
	public void create(ListJS args)
	{
		if (args.size() != 2)
		{
			throw new RecipeExceptionJS("Stonecutting recipe requires 2 arguments - result and ingredient!");
		}

		result = ItemStackJS.of(args.get(0));

		if (result.isEmpty())
		{
			throw new RecipeExceptionJS("Stonecutting recipe result " + args.get(0) + " is not a valid item!");
		}

		ingredient = IngredientJS.of(args.get(1));

		if (ingredient.isEmpty())
		{
			throw new RecipeExceptionJS("Stonecutting recipe ingredient " + args.get(1) + " is not a valid ingredient!");
		}
	}

	@Override
	public void deserialize()
	{
		result = ItemStackJS.resultFromRecipeJson(json.get("result"));

		if (result.isEmpty())
		{
			throw new RecipeExceptionJS("Stonecutting recipe result " + json.get("result") + " is not a valid item!");
		}

		ingredient = IngredientJS.ingredientFromRecipeJson(json.get("ingredient"));

		if (ingredient.isEmpty())
		{
			throw new RecipeExceptionJS("Stonecutting recipe ingredient " + json.get("ingredient") + " is not a valid ingredient!");
		}
	}

	@Override
	public void serialize()
	{
		json.add("ingredient", ingredient.toJson());
		json.addProperty("result", result.getId().toString());
		json.addProperty("count", result.getCount());
	}

	@Override
	public Collection<IngredientJS> getInput()
	{
		return Collections.singleton(ingredient);
	}

	@Override
	public boolean replaceInput(Object i, Object with)
	{
		if (ingredient.anyStackMatches(IngredientJS.of(i)))
		{
			ingredient = IngredientJS.of(with);
			save();
			return true;
		}

		return false;
	}

	@Override
	public Collection<ItemStackJS> getOutput()
	{
		return Collections.singleton(result);
	}

	@Override
	public boolean replaceOutput(Object i, Object with)
	{
		if (IngredientJS.of(i).test(result))
		{
			result = ItemStackJS.of(with);
			save();
			return true;
		}

		return false;
	}
}