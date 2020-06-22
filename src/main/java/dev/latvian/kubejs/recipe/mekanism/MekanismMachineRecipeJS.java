package dev.latvian.kubejs.recipe.mekanism;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.kubejs.recipe.RecipeJS;
import dev.latvian.kubejs.util.ListJS;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class MekanismMachineRecipeJS extends RecipeJS
{
	public List<Integer> inputAmount = new ArrayList<>();
	private String inputName = "input";
	private String outputName = "output";

	@Override
	public void create(ListJS args)
	{
		ItemStackJS output = ItemStackJS.of(args.get(0));

		if (output.isEmpty())
		{
			throw new RecipeExceptionJS("Mekanism machine recipe result can't be empty!");
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
					inputAmount.add(c);
				}
				else
				{
					inputItems.add(i);
					inputAmount.add(1);
				}
			}
		}

		if (inputItems.isEmpty())
		{
			throw new RecipeExceptionJS("Mekanism machine recipe ingredient " + args.get(1) + " is not a valid ingredient!");
		}
	}

	@Override
	public void deserialize()
	{
		ItemStackJS output = ItemStackJS.resultFromRecipeJson(json.get("output"));

		if (output.isEmpty())
		{
			output = ItemStackJS.resultFromRecipeJson(json.get("mainOutput"));

			if (output.isEmpty())
			{
				throw new RecipeExceptionJS("Mekanism machine recipe result can't be empty!");
			}
			else
			{
				outputName = "mainOutput";
			}
		}

		outputItems.add(output);

		JsonElement in = json.get("input");

		if (in == null || in.isJsonNull())
		{
			in = json.get("itemInput");
			inputName = "itemInput";
		}

		if (in instanceof JsonArray)
		{
			for (JsonElement e : (JsonArray) in)
			{
				JsonObject o = e.getAsJsonObject();
				IngredientJS i = IngredientJS.ingredientFromRecipeJson(o.get("ingredient"));

				if (!i.isEmpty())
				{
					inputItems.add(i.count(1));

					if (o.has("amount"))
					{
						inputAmount.add(o.get("amount").getAsInt());
					}
					else
					{
						inputAmount.add(1);
					}
				}
			}
		}
		else if (in instanceof JsonObject)
		{
			JsonObject o = in.getAsJsonObject();
			IngredientJS i = IngredientJS.ingredientFromRecipeJson(o.get("ingredient"));

			if (!i.isEmpty())
			{
				inputItems.add(i.count(1));

				if (o.has("amount"))
				{
					inputAmount.add(o.get("amount").getAsInt());
				}
				else
				{
					inputAmount.add(1);
				}
			}
		}

		if (inputItems.isEmpty())
		{
			throw new RecipeExceptionJS("Mekanism machine recipe ingredient " + json.get("ingredient") + " is not a valid ingredient!");
		}
	}

	@Override
	public void serialize()
	{
		if (inputItems.size() == 1)
		{
			JsonObject inputJson = new JsonObject();
			inputJson.add("ingredient", inputItems.get(0).toJson());
			inputJson.addProperty("amount", inputAmount.get(0));
			json.add(inputName, inputJson);
		}
		else
		{
			JsonArray inputArray = new JsonArray();

			for (int i = 0; i < inputItems.size(); i++)
			{
				JsonObject inputJson = new JsonObject();
				inputJson.add("ingredient", inputItems.get(i).toJson());
				inputJson.addProperty("amount", inputAmount.get(i));
				inputArray.add(inputJson);
			}

			json.add(inputName, inputArray);
		}

		json.add(outputName, outputItems.get(0).toResultJson());
	}
}