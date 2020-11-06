package dev.latvian.kubejs.recipe.mekanism;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.kubejs.recipe.RecipeJS;
import dev.latvian.kubejs.util.ListJS;
import org.apache.commons.lang3.tuple.Pair;

import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class MekanismMachineRecipeJS extends RecipeJS
{
	public List<Integer> inputAmount = new ArrayList<>();
	public final String inputName;
	public final String outputName;

	public MekanismMachineRecipeJS(String in, String out)
	{
		inputName = in;
		outputName = out;
	}

	public MekanismMachineRecipeJS()
	{
		this("input", "output");
	}

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

	public static List<Pair<IngredientJS, Integer>> deserializeIngredient(@Nullable JsonElement json)
	{
		List<Pair<IngredientJS, Integer>> list = new ArrayList<>();

		if (json instanceof JsonArray)
		{
			for (JsonElement e : (JsonArray) json)
			{
				list.addAll(deserializeIngredient(e));
			}
		}
		else if (json instanceof JsonObject)
		{
			JsonObject o = json.getAsJsonObject();
			IngredientJS i = IngredientJS.ingredientFromRecipeJson(o.get("ingredient"));

			if (!i.isEmpty())
			{
				int c;

				if (o.has("amount"))
				{
					c = o.get("amount").getAsInt();
				}
				else
				{
					c = 1;
				}

				list.add(Pair.of(i.count(1), c));
			}
		}

		return list;
	}

	@Override
	public void deserialize()
	{
		ItemStackJS output = ItemStackJS.resultFromRecipeJson(json.get(outputName));

		if (output.isEmpty())
		{
			throw new RecipeExceptionJS("Mekanism machine recipe result can't be empty!");
		}

		outputItems.add(output);

		for (Pair<IngredientJS, Integer> pair : deserializeIngredient(json.get(inputName)))
		{
			inputItems.add(pair.getLeft());
			inputAmount.add(pair.getRight());
		}

		if (inputItems.isEmpty())
		{
			throw new RecipeExceptionJS("Mekanism machine recipe ingredient " + json.get(inputName) + " is not a valid ingredient!");
		}
	}

	public static JsonObject serializeIngredient(IngredientJS ingredient, int amount)
	{
		JsonObject json = new JsonObject();
		json.add("ingredient", ingredient.toJson());

		if (amount > 1)
		{
			json.addProperty("amount", amount);
		}

		return json;
	}

	@Override
	public void serialize()
	{
		if (inputItems.size() == 1)
		{
			json.add(inputName, serializeIngredient(inputItems.get(0), inputAmount.get(0)));
		}
		else
		{
			JsonArray inputArray = new JsonArray();

			for (int i = 0; i < inputItems.size(); i++)
			{
				inputArray.add(serializeIngredient(inputItems.get(i), inputAmount.get(i)));
			}

			json.add(inputName, inputArray);
		}

		json.add(outputName, outputItems.get(0).toResultJson());
	}
}