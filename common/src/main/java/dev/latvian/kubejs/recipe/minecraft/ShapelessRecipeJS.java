package dev.latvian.kubejs.recipe.minecraft;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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

		for (Object o : ListJS.orSelf(args.get(1)))
		{
			inputItems.add(parseIngredientItem(o));
		}

		if (inputItems.isEmpty())
		{
			throw new RecipeExceptionJS("Ingredient list can't be empty!");
		}
	}

	@Override
	public void deserialize()
	{
		outputItems.add(parseResultItem(json.get("result")));

		for (JsonElement e : json.get("ingredients").getAsJsonArray())
		{
			inputItems.add(parseIngredientItem(e));
		}

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