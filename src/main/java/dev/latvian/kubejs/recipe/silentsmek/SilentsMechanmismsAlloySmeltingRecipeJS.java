package dev.latvian.kubejs.recipe.silentsmek;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.item.ingredient.MatchAnyIngredientJS;
import dev.latvian.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.kubejs.recipe.RecipeJS;
import dev.latvian.kubejs.util.ListJS;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class SilentsMechanmismsAlloySmeltingRecipeJS extends RecipeJS
{
	public List<Integer> inputCount = new ArrayList<>();

	@Override
	public void create(ListJS args)
	{
		ItemStackJS output = ItemStackJS.of(args.get(0));

		if (output.isEmpty())
		{
			throw new RecipeExceptionJS("Silents Mechanisms alloy smelting recipe result can't be empty!");
		}

		outputItems.add(output);

		ListJS in = ListJS.orSelf(args.get(1));

		for (Object o : in)
		{
			IngredientJS i = IngredientJS.of(o);

			if (!i.isEmpty())
			{
				int c = i.getCount();

				if (c > 1)
				{
					inputItems.add(i.count(1));
					inputCount.add(c);
				}
				else
				{
					inputItems.add(i);
					inputCount.add(1);
				}
			}
		}

		if (inputItems.isEmpty())
		{
			throw new RecipeExceptionJS("Silents Mechanisms alloy smelting recipe ingredient " + args.get(1) + " is not a valid ingredient!");
		}
	}

	@Override
	public void deserialize()
	{
		ItemStackJS result = ItemStackJS.resultFromRecipeJson(json.get("result"));

		if (result.isEmpty())
		{
			throw new RecipeExceptionJS("Silents Mechanisms alloy smelting recipe result can't be empty!");
		}

		outputItems.add(result);

		for (JsonElement e : json.get("ingredients").getAsJsonArray())
		{
			JsonObject o = e.getAsJsonObject();
			MatchAnyIngredientJS l = new MatchAnyIngredientJS();

			for (JsonElement e1 : o.get("values").getAsJsonArray())
			{
				IngredientJS i = IngredientJS.ingredientFromRecipeJson(e1);

				if (!i.isEmpty())
				{
					l.ingredients.add(i);
				}
			}

			if (!l.isEmpty())
			{
				inputItems.add(l.ingredients.size() == 1 ? l.ingredients.get(0) : l);
				inputCount.add(o.has("count") ? o.get("count").getAsInt() : 1);
			}
		}

		if (inputItems.isEmpty())
		{
			throw new RecipeExceptionJS("Silents Mechanisms alloy smelting recipe ingredient " + json.get("ingredient") + " is not a valid ingredient!");
		}
	}

	@Override
	public void serialize()
	{
		JsonArray ingredientsJson = new JsonArray();

		for (int i = 0; i < inputItems.size(); i++)
		{
			JsonObject ingredientJson = new JsonObject();

			JsonArray valuesJson = new JsonArray();

			if (inputItems.get(i) instanceof MatchAnyIngredientJS)
			{
				for (IngredientJS in : ((MatchAnyIngredientJS) inputItems.get(i)).ingredients)
				{
					valuesJson.add(in.toJson());
				}
			}
			else
			{
				valuesJson.add(inputItems.get(i).toJson());
			}

			ingredientJson.add("values", valuesJson);
			ingredientJson.addProperty("count", inputCount.get(i));
			ingredientsJson.add(ingredientJson);
		}

		json.add("ingredients", ingredientsJson);
		json.add("result", outputItems.get(0).getResultJson());
	}
}