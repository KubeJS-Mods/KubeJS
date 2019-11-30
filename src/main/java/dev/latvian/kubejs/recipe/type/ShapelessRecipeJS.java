package dev.latvian.kubejs.recipe.type;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.item.EmptyItemStackJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.recipe.RecipeErrorJS;
import dev.latvian.kubejs.recipe.RecipeTypeJS;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.item.crafting.IRecipeSerializer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author LatvianModder
 */
public class ShapelessRecipeJS extends RecipeJS
{
	public static final RecipeTypeJS TYPE = new RecipeTypeJS(IRecipeSerializer.CRAFTING_SHAPELESS)
	{
		@Override
		public RecipeJS create(Object[] args)
		{
			if (args.length != 2)
			{
				return new RecipeErrorJS("Shapeless recipe requires 2 arguments - result and ingredients!");
			}

			ShapelessRecipeJS recipe = new ShapelessRecipeJS();
			recipe.result = ItemStackJS.of(args[0]);

			if (recipe.result.isEmpty())
			{
				return new RecipeErrorJS("Shapeless recipe result " + args[0] + " is not a valid item!");
			}

			List ingredients = UtilsJS.getNormalizedListOrSelf(args[1]);

			if (!(args[1] instanceof Collection) || ((Collection) args[1]).isEmpty())
			{
				return new RecipeErrorJS("Shapeless recipe ingredient list is empty!");
			}

			for (Object o : (Collection) args[1])
			{
				IngredientJS in = IngredientJS.of(o);

				if (!in.isEmpty())
				{
					recipe.ingredients.add(in);
				}
				else
				{
					return new RecipeErrorJS("Shapeless recipe ingredient " + o + " is not a valid ingredient!");
				}
			}

			return recipe;
		}

		@Override
		public RecipeJS create(JsonObject json)
		{
			ShapelessRecipeJS recipe = new ShapelessRecipeJS();

			recipe.result = ItemStackJS.fromRecipeJson(json.get("result"));

			if (recipe.result.isEmpty())
			{
				return new RecipeErrorJS("Shapeless recipe result " + json.get("result") + " is not a valid item!");
			}

			for (JsonElement e : json.get("ingredients").getAsJsonArray())
			{
				IngredientJS in = IngredientJS.fromRecipeJson(e);

				if (!in.isEmpty())
				{
					recipe.ingredients.add(in);
				}
				else
				{
					return new RecipeErrorJS("Shapeless recipe ingredient " + e + " is not a valid ingredient!");
				}
			}

			if (recipe.ingredients.isEmpty())
			{
				return new RecipeErrorJS("Shapeless recipe ingredient list is empty!");
			}

			return recipe;
		}
	};

	public final List<IngredientJS> ingredients = new ArrayList<>();
	public ItemStackJS result = EmptyItemStackJS.INSTANCE;

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

		for (IngredientJS in : ingredients)
		{
			ingredientsJson.add(in.getJson());
		}

		json.add("ingredients", ingredientsJson);
		json.add("result", result.getResultJson());
		return json;
	}

	@Override
	public boolean hasInput(IngredientJS ingredient)
	{
		for (IngredientJS in : ingredients)
		{
			if (in.anyStackMatches(ingredient))
			{
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean hasOutput(IngredientJS ingredient)
	{
		return ingredient.test(result);
	}
}