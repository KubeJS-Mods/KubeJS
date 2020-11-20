package dev.latvian.kubejs.recipe.minecraft;

import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.kubejs.recipe.RecipeJS;
import dev.latvian.kubejs.util.ListJS;

/**
 * @author LatvianModder
 */
public class SmithingRecipeJS extends RecipeJS
{
	@Override
	public void create(ListJS args)
	{
		ItemStackJS result = ItemStackJS.of(args.get(0));

		if (result.isEmpty())
		{
			throw new RecipeExceptionJS("Smithing recipe result " + args.get(0) + " is not a valid item!");
		}

		outputItems.add(result);

		IngredientJS ingredient1 = IngredientJS.of(args.get(1));

		if (ingredient1.isEmpty())
		{
			throw new RecipeExceptionJS("Smithing recipe ingredient " + args.get(1) + " is not a valid ingredient!");
		}

		inputItems.add(ingredient1);

		IngredientJS ingredient2 = IngredientJS.of(args.get(2));

		if (ingredient2.isEmpty())
		{
			throw new RecipeExceptionJS("Smithing recipe ingredient " + args.get(1) + " is not a valid ingredient!");
		}

		inputItems.add(ingredient2);
	}

	@Override
	public void deserialize()
	{
		ItemStackJS result = ItemStackJS.resultFromRecipeJson(json.get("result"));

		if (result.isEmpty())
		{
			throw new RecipeExceptionJS("Smithing recipe result " + json.get("result") + " is not a valid item!");
		}

		outputItems.add(result);

		IngredientJS ingredient1 = IngredientJS.ingredientFromRecipeJson(json.get("base"));

		if (ingredient1.isEmpty())
		{
			throw new RecipeExceptionJS("Smithing recipe ingredient " + json.get("base") + " is not a valid ingredient!");
		}

		inputItems.add(ingredient1);

		IngredientJS ingredient2 = IngredientJS.ingredientFromRecipeJson(json.get("addition"));

		if (ingredient2.isEmpty())
		{
			throw new RecipeExceptionJS("Smithing recipe ingredient " + json.get("addition") + " is not a valid ingredient!");
		}

		inputItems.add(ingredient2);
	}

	@Override
	public void serialize()
	{
		json.add("base", inputItems.get(0).toJson());
		json.add("addition", inputItems.get(1).toJson());
		json.add("result", outputItems.get(0).toResultJson());
	}
}