package dev.latvian.kubejs.integration.create;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.item.EmptyItemStackJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.recipe.RecipeTypeJS;
import dev.latvian.kubejs.recipe.type.RecipeJS;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class CreateModMachineRecipeJS extends RecipeJS
{
	public final RecipeTypeJS recipeType;
	public IngredientJS ingredient = EmptyItemStackJS.INSTANCE;
	public List<ItemStackJS> results = new ArrayList<>();
	public int processingTime = 300;

	public CreateModMachineRecipeJS(RecipeTypeJS rt)
	{
		recipeType = rt;
	}

	@Override
	public RecipeTypeJS getType()
	{
		return recipeType;
	}

	@Override
	public JsonObject toJson()
	{
		JsonObject json = create();

		JsonArray ingredientsJson = new JsonArray();
		ingredientsJson.add(ingredient.toJson());
		json.add("ingredients", ingredientsJson);

		JsonArray resultsJson = new JsonArray();

		for (ItemStackJS stack : results)
		{
			resultsJson.add(stack.getResultJson());
		}

		json.add("results", resultsJson);
		json.addProperty("processingTime", processingTime);
		return json;
	}

	public CreateModMachineRecipeJS time(int t)
	{
		processingTime = Math.max(t, 0);
		return this;
	}

	@Override
	public boolean hasInput(Object i)
	{
		return ingredient.anyStackMatches(IngredientJS.of(i));
	}

	@Override
	public boolean hasOutput(Object i)
	{
		for (ItemStackJS result : results)
		{
			if (IngredientJS.of(i).test(result))
			{
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean replaceInput(Object i, Object with)
	{
		if (ingredient.anyStackMatches(IngredientJS.of(i)))
		{
			ingredient = IngredientJS.of(with);
			return true;
		}

		return false;
	}

	@Override
	public boolean replaceOutput(Object i, Object with)
	{
		boolean changed = false;

		for (int j = 0; j < results.size(); j++)
		{
			if (IngredientJS.of(i).test(results.get(j)))
			{
				results.set(j, ItemStackJS.of(with));
				changed = true;
			}
		}

		return changed;
	}
}