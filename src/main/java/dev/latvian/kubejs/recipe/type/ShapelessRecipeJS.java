package dev.latvian.kubejs.recipe.type;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import dev.latvian.kubejs.item.EmptyItemStackJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.kubejs.util.ListJS;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author LatvianModder
 */
public class ShapelessRecipeJS extends RecipeJS
{
	private final List<IngredientJS> ingredients = new ArrayList<>();
	private ItemStackJS result = EmptyItemStackJS.INSTANCE;

	@Override
	public void create(ListJS args)
	{
		if (args.size() != 2)
		{
			throw new RecipeExceptionJS("Shapeless recipe requires 2 arguments - result and ingredients!");
		}

		result = ItemStackJS.of(args.get(0));

		if (result.isEmpty())
		{
			throw new RecipeExceptionJS("Shapeless recipe result " + args.get(0) + " is not a valid item!");
		}

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
				ingredients.add(in);
			}
			else
			{
				throw new RecipeExceptionJS("Shapeless recipe ingredient " + o + " is not a valid ingredient!");
			}
		}
	}

	@Override
	public void deserialize()
	{
		result = ItemStackJS.resultFromRecipeJson(json.get("result"));

		if (result.isEmpty())
		{
			throw new RecipeExceptionJS("Shapeless recipe result " + json.get("result") + " is not a valid item!");
		}

		for (JsonElement e : json.get("ingredients").getAsJsonArray())
		{
			IngredientJS in = IngredientJS.ingredientFromRecipeJson(e);

			if (!in.isEmpty())
			{
				ingredients.add(in);
			}
			else
			{
				throw new RecipeExceptionJS("Shapeless recipe ingredient " + e + " is not a valid ingredient!");
			}
		}

		if (ingredients.isEmpty())
		{
			throw new RecipeExceptionJS("Shapeless recipe ingredient list is empty!");
		}
	}

	@Override
	public void serialize()
	{
		JsonArray ingredientsJson = new JsonArray();

		for (IngredientJS in : ingredients)
		{
			ingredientsJson.add(in.toJson());
		}

		json.add("ingredients", ingredientsJson);
		json.add("result", result.getResultJson());
	}

	@Override
	public Collection<IngredientJS> getInput()
	{
		return ingredients;
	}

	@Override
	public boolean replaceInput(Object i, Object with)
	{
		boolean changed = false;

		for (int j = 0; j < ingredients.size(); j++)
		{
			if (ingredients.get(j).anyStackMatches(IngredientJS.of(i)))
			{
				ingredients.set(j, IngredientJS.of(with));
				changed = true;
				save();
			}
		}

		return changed;
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
			result = ItemStackJS.of(with).count(result.getCount());
			save();
			return true;
		}

		return false;
	}
}