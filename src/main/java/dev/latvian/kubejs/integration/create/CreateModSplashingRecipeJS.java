package dev.latvian.kubejs.integration.create;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.item.EmptyItemStackJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.recipe.RecipeErrorJS;
import dev.latvian.kubejs.recipe.RecipeTypeJS;
import dev.latvian.kubejs.recipe.type.RecipeJS;
import dev.latvian.kubejs.util.ListJS;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class CreateModSplashingRecipeJS extends RecipeJS
{
	public static final RecipeTypeJS TYPE = new RecipeTypeJS(new ResourceLocation("create:splashing"))
	{
		@Override
		public RecipeJS create(ListJS args)
		{
			if (args.size() < 2)
			{
				return new RecipeErrorJS("Create splashing recipe requires at least 2 arguments - result array and ingredient!");
			}

			CreateModSplashingRecipeJS recipe = new CreateModSplashingRecipeJS();

			ListJS results = ListJS.orSelf(args.get(0));

			if (results.isEmpty())
			{
				return new RecipeErrorJS("Create splashing recipe results can't be empty!");
			}

			for (Object o : results)
			{
				ItemStackJS stack = ItemStackJS.of(o);

				if (stack.isEmpty())
				{
					return new RecipeErrorJS("Create splashing recipe result " + o + " is not a valid item!");
				}
				else
				{
					recipe.results.add(stack);
				}
			}

			recipe.ingredient = IngredientJS.of(args.get(1));

			if (recipe.ingredient.isEmpty())
			{
				return new RecipeErrorJS("Create splashing recipe ingredient " + args.get(1) + " is not a valid ingredient!");
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
			CreateModSplashingRecipeJS recipe = new CreateModSplashingRecipeJS();

			for (JsonElement e : json.get("results").getAsJsonArray())
			{
				ItemStackJS stack = ItemStackJS.fromRecipeJson(e);

				if (stack.isEmpty())
				{
					return new RecipeErrorJS("Create splashing recipe result " + e + " is not a valid item!");
				}
				else
				{
					recipe.results.add(stack);
				}
			}

			if (recipe.results.isEmpty())
			{
				return new RecipeErrorJS("Create splashing recipe results can't be empty!");
			}

			JsonElement in = json.get("ingredients").getAsJsonArray().get(0);
			recipe.ingredient = IngredientJS.fromRecipeJson(in);

			if (recipe.ingredient.isEmpty())
			{
				return new RecipeErrorJS("Create splashing recipe ingredient " + in + " is not a valid ingredient!");
			}

			recipe.processingTime = json.get("processingTime").getAsInt();
			return recipe;
		}
	};

	private IngredientJS ingredient = EmptyItemStackJS.INSTANCE;
	private List<ItemStackJS> results = new ArrayList<>();
	private int processingTime = 300;

	@Override
	public RecipeTypeJS getType()
	{
		return TYPE;
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

	public CreateModSplashingRecipeJS time(int t)
	{
		processingTime = Math.max(t, 0);
		return this;
	}

	@Override
	public boolean hasInput(IngredientJS ingredient)
	{
		return ingredient.anyStackMatches(ingredient);
	}

	@Override
	public boolean hasOutput(IngredientJS ingredient)
	{
		for (ItemStackJS stack : results)
		{
			if (ingredient.test(stack))
			{
				return true;
			}
		}

		return false;
	}
}
