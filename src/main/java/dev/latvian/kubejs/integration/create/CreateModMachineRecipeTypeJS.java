package dev.latvian.kubejs.integration.create;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.recipe.RecipeErrorJS;
import dev.latvian.kubejs.recipe.RecipeTypeJS;
import dev.latvian.kubejs.recipe.type.RecipeJS;
import dev.latvian.kubejs.util.ListJS;
import net.minecraft.util.ResourceLocation;

/**
 * @author LatvianModder
 */
public class CreateModMachineRecipeTypeJS extends RecipeTypeJS
{
	public CreateModMachineRecipeTypeJS(ResourceLocation i)
	{
		super(i);
	}

	@Override
	public RecipeJS create(ListJS args)
	{
		if (args.size() < 2)
		{
			return new RecipeErrorJS("Create machine recipe requires at least 2 arguments - result array and ingredient!");
		}

		CreateModMachineRecipeJS recipe = new CreateModMachineRecipeJS(this);

		ListJS results = ListJS.orSelf(args.get(0));

		if (results.isEmpty())
		{
			return new RecipeErrorJS("Create machine recipe results can't be empty!");
		}

		for (Object o : results)
		{
			ItemStackJS stack = ItemStackJS.of(o);

			if (stack.isEmpty())
			{
				return new RecipeErrorJS("Create machine recipe result " + o + " is not a valid item!");
			}
			else
			{
				recipe.results.add(stack);
			}
		}

		recipe.ingredient = IngredientJS.of(args.get(1));

		if (recipe.ingredient.isEmpty())
		{
			return new RecipeErrorJS("Create machine recipe ingredient " + args.get(1) + " is not a valid ingredient!");
		}

		if (args.size() >= 3)
		{
			recipe.processingTime = ((Number) args.get(2)).intValue();
		}

		return recipe;
	}

	@Override
	public RecipeJS create(JsonObject json)
	{
		CreateModMachineRecipeJS recipe = new CreateModMachineRecipeJS(this);

		for (JsonElement e : json.get("results").getAsJsonArray())
		{
			ItemStackJS stack = ItemStackJS.resultFromRecipeJson(e);

			if (stack.isEmpty())
			{
				return new RecipeErrorJS("Create machine recipe result " + e + " is not a valid item!");
			}
			else
			{
				recipe.results.add(stack);
			}
		}

		if (recipe.results.isEmpty())
		{
			return new RecipeErrorJS("Create machine recipe results can't be empty!");
		}

		JsonElement in = json.get("ingredients").getAsJsonArray().get(0);
		recipe.ingredient = IngredientJS.ingredientFromRecipeJson(in);

		if (recipe.ingredient.isEmpty())
		{
			return new RecipeErrorJS("Create machine recipe ingredient " + in + " is not a valid ingredient!");
		}

		recipe.processingTime = json.get("processingTime").getAsInt();
		return recipe;
	}
}
