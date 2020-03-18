package dev.latvian.kubejs.recipe.create;

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
public class CreateMachineRecipeJS extends RecipeJS
{
	@Override
	public void create(ListJS args)
	{
		ListJS results1 = ListJS.orSelf(args.get(0));

		if (results1.isEmpty())
		{
			throw new RecipeExceptionJS("Create machine recipe results can't be empty!");
		}

		for (Object o : results1)
		{
			ItemStackJS stack = ItemStackJS.of(o);

			if (stack.isEmpty())
			{
				throw new RecipeExceptionJS("Create machine recipe result " + o + " is not a valid item!");
			}
			else
			{
				outputItems.add(stack);
			}
		}

		IngredientJS ingredient = IngredientJS.of(args.get(1));

		if (ingredient.isEmpty())
		{
			throw new RecipeExceptionJS("Create machine recipe ingredient " + args.get(1) + " is not a valid ingredient!");
		}

		inputItems.add(ingredient);

		if (args.size() >= 3)
		{
			time(((Number) args.get(2)).intValue());
		}
	}

	@Override
	public void deserialize()
	{
		for (JsonElement e : json.get("results").getAsJsonArray())
		{
			ItemStackJS stack = ItemStackJS.resultFromRecipeJson(e);

			if (stack.isEmpty())
			{
				throw new RecipeExceptionJS("Create machine recipe result " + e + " is not a valid item!");
			}
			else
			{
				outputItems.add(stack);
			}
		}

		if (outputItems.isEmpty())
		{
			throw new RecipeExceptionJS("Create machine recipe results can't be empty!");
		}

		JsonElement in = json.get("ingredients").getAsJsonArray().get(0);
		IngredientJS ingredient = IngredientJS.ingredientFromRecipeJson(in);

		if (ingredient.isEmpty())
		{
			throw new RecipeExceptionJS("Create machine recipe ingredient " + in + " is not a valid ingredient!");
		}

		inputItems.add(ingredient);
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

		JsonArray resultsJson = new JsonArray();

		for (ItemStackJS stack : outputItems)
		{
			resultsJson.add(stack.getResultJson());
		}

		json.add("results", resultsJson);

		if (!json.has("processingTime"))
		{
			json.addProperty("processingTime", 300);
		}
	}

	public CreateMachineRecipeJS time(int t)
	{
		json.addProperty("processingTime", Math.max(t, 0));
		return this;
	}
}