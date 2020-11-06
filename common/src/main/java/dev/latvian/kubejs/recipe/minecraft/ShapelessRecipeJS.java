package dev.latvian.kubejs.recipe.minecraft;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.kubejs.recipe.RecipeJS;
import dev.latvian.kubejs.util.ListJS;

/**
 * @author LatvianModder
 */
public class ShapelessRecipeJS extends RecipeJS
{
	@Override
	public void create(ListJS args)
	{
		ItemStackJS result = ItemStackJS.of(args.get(0));

		if (result.isEmpty())
		{
			throw new RecipeExceptionJS("Shapeless recipe result " + args.get(0) + " is not a valid item!");
		}

		outputItems.add(result);

		ListJS ingredients1 = ListJS.orSelf(args.get(1));

		if (ingredients1.isEmpty())
		{
			throw new RecipeExceptionJS("Shapeless recipe ingredient list is empty!");
		}

		for (Object o : ingredients1)
		{
			IngredientJS in = IngredientJS.of(o);

			if (!in.isEmpty())
			{
				inputItems.add(in);
			}
			else
			{
				throw new RecipeExceptionJS("Shapeless recipe ingredient " + o + " is not a valid ingredient!");
			}
		}

		if (inputItems.isEmpty())
		{
			throw new RecipeExceptionJS("Shapeless recipe ingredient list is empty!");
		}
	}

	@Override
	public void deserialize()
	{
		ItemStackJS result = ItemStackJS.resultFromRecipeJson(json.get("result"));

		if (result.isEmpty())
		{
			throw new RecipeExceptionJS("Shapeless recipe result " + json.get("result") + " is not a valid item!");
		}

		outputItems.add(result);

		for (JsonElement e : json.get("ingredients").getAsJsonArray())
		{
			IngredientJS in = IngredientJS.ingredientFromRecipeJson(e);

			if (!in.isEmpty())
			{
				inputItems.add(in);
			}
			else
			{
				throw new RecipeExceptionJS("Shapeless recipe ingredient " + e + " is not a valid ingredient!");
			}
		}

		if (inputItems.isEmpty())
		{
			throw new RecipeExceptionJS("Shapeless recipe ingredient list is empty!");
		}
	}

	@Override
	public void serialize()
	{
		JsonArray ingredientsJson = new JsonArray();

		for (IngredientJS in : inputItems)
		{
			ingredientsJson.add(in.toJson());
		}

		json.add("ingredients", ingredientsJson);
		json.add("result", outputItems.get(0).toResultJson());
	}
}