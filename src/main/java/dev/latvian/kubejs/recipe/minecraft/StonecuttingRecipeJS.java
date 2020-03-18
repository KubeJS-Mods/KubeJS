package dev.latvian.kubejs.recipe.minecraft;

import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.kubejs.recipe.RecipeJS;
import dev.latvian.kubejs.util.ListJS;

/**
 * @author LatvianModder
 */
public class StonecuttingRecipeJS extends RecipeJS
{
	@Override
	public void create(ListJS args)
	{
		ItemStackJS result = ItemStackJS.of(args.get(0));

		if (result.isEmpty())
		{
			throw new RecipeExceptionJS("Stonecutting recipe result " + args.get(0) + " is not a valid item!");
		}

		outputItems.add(result);

		IngredientJS ingredient = IngredientJS.of(args.get(1));

		if (ingredient.isEmpty())
		{
			throw new RecipeExceptionJS("Stonecutting recipe ingredient " + args.get(1) + " is not a valid ingredient!");
		}

		inputItems.add(ingredient);
	}

	@Override
	public void deserialize()
	{
		ItemStackJS result = ItemStackJS.resultFromRecipeJson(json.get("result"));

		if (result.isEmpty())
		{
			throw new RecipeExceptionJS("Stonecutting recipe result " + json.get("result") + " is not a valid item!");
		}

		outputItems.add(result);

		IngredientJS ingredient = IngredientJS.ingredientFromRecipeJson(json.get("ingredient"));

		if (ingredient.isEmpty())
		{
			throw new RecipeExceptionJS("Stonecutting recipe ingredient " + json.get("ingredient") + " is not a valid ingredient!");
		}

		inputItems.add(ingredient);
	}

	@Override
	public void serialize()
	{
		json.add("ingredient", inputItems.get(0).toJson());
		json.addProperty("result", outputItems.get(0).getId().toString());
		json.addProperty("count", outputItems.get(0).getCount());
	}
}