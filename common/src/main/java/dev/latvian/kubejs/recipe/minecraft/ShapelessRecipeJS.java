package dev.latvian.kubejs.recipe.minecraft;

import com.google.gson.JsonArray;
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
		outputItems.add(parseResultItem(args.get(0)));
		inputItems.addAll(parseIngredientItemList(args.get(1)));

		if (inputItems.isEmpty())
		{
			throw new RecipeExceptionJS("Ingredient list can't be empty!");
		}
	}

	@Override
	public void deserialize()
	{
		outputItems.add(parseResultItem(json.get("result")));
		inputItems.addAll(parseIngredientItemList(json.get("ingredients")));

		if (inputItems.isEmpty())
		{
			throw new RecipeExceptionJS("Ingredient list can't be empty!");
		}
	}

	@Override
	public void serialize()
	{
		if (serializeInputs)
		{
			JsonArray ingredientsJson = new JsonArray();

			for (IngredientJS in : inputItems)
			{
				ingredientsJson.add(in.toJson());
			}

			json.add("ingredients", ingredientsJson);
		}

		if (serializeOutputs)
		{
			json.add("result", outputItems.get(0).toResultJson());
		}
	}
}