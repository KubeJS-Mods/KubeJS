package dev.latvian.kubejs.recipe.type;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.item.EmptyItemStackJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.recipe.RecipeErrorJS;
import dev.latvian.kubejs.recipe.RecipeTypeJS;
import dev.latvian.kubejs.util.ListJS;
import net.minecraft.item.crafting.IRecipeSerializer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class ShapelessRecipeJS extends RecipeJS
{
	public static final RecipeTypeJS TYPE = new RecipeTypeJS(IRecipeSerializer.CRAFTING_SHAPELESS)
	{
		@Override
		public RecipeJS create(ListJS args)
		{
			if (args.size() != 2)
			{
				return new RecipeErrorJS("Shapeless recipe requires 2 arguments - result and ingredients!");
			}

			ShapelessRecipeJS recipe = new ShapelessRecipeJS();
			recipe.result = ItemStackJS.of(args.get(0));

			if (recipe.result.isEmpty())
			{
				return new RecipeErrorJS("Shapeless recipe result " + args.get(0) + " is not a valid item!");
			}

			ListJS ingredients = ListJS.orSelf(args.get(1));

			if (ingredients.isEmpty())
			{
				return new RecipeErrorJS("Shapeless recipe ingredient list is empty!");
			}

			for (Object o : ingredients)
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

			recipe.result = ItemStackJS.resultFromRecipeJson(json.get("result"));

			if (recipe.result.isEmpty())
			{
				return new RecipeErrorJS("Shapeless recipe result " + json.get("result") + " is not a valid item!");
			}

			for (JsonElement e : json.get("ingredients").getAsJsonArray())
			{
				IngredientJS in = IngredientJS.ingredientFromRecipeJson(e);

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

	private final List<IngredientJS> ingredients = new ArrayList<>();
	private ItemStackJS result = EmptyItemStackJS.INSTANCE;

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
			ingredientsJson.add(in.toJson());
		}

		json.add("ingredients", ingredientsJson);
		json.add("result", result.getResultJson());
		return json;
	}

	@Override
	public boolean hasInput(Object i)
	{
		for (IngredientJS in : ingredients)
		{
			if (in.anyStackMatches(IngredientJS.of(i)))
			{
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean hasOutput(Object i)
	{
		return IngredientJS.of(i).test(result);
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
			}
		}

		return changed;
	}

	@Override
	public boolean replaceOutput(Object i, Object with)
	{
		if (IngredientJS.of(i).test(result))
		{
			result = ItemStackJS.of(with);
			return true;
		}

		return false;
	}
}