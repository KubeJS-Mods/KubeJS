package dev.latvian.kubejs.recipe.type;

import com.google.gson.JsonObject;
import dev.latvian.kubejs.item.EmptyItemStackJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.recipe.RecipeErrorJS;
import dev.latvian.kubejs.recipe.RecipeTypeJS;
import net.minecraft.item.crafting.IRecipeSerializer;

/**
 * @author LatvianModder
 */
public class StonecuttingRecipeJS extends RecipeJS
{
	public static final RecipeTypeJS TYPE = new RecipeTypeJS(IRecipeSerializer.STONECUTTING)
	{
		@Override
		public RecipeJS create(Object[] args)
		{
			if (args.length != 2)
			{
				return new RecipeErrorJS("Stonecutting recipe requires 2 arguments - result and ingredient!");
			}

			StonecuttingRecipeJS recipe = new StonecuttingRecipeJS();
			recipe.result = ItemStackJS.of(args[0]);

			if (recipe.result.isEmpty())
			{
				return new RecipeErrorJS("Stonecutting recipe result " + args[0] + " is not a valid item!");
			}

			recipe.ingredient = IngredientJS.of(args[1]);

			if (recipe.ingredient.isEmpty())
			{
				return new RecipeErrorJS("Stonecutting recipe ingredient " + args[1] + " is not a valid ingredient!");
			}

			return recipe;
		}

		@Override
		public RecipeJS create(JsonObject json)
		{
			StonecuttingRecipeJS recipe = new StonecuttingRecipeJS();
			recipe.result = ItemStackJS.fromRecipeJson(json.get("result"));

			if (recipe.result.isEmpty())
			{
				return new RecipeErrorJS("Stonecutting recipe result " + json.get("result") + " is not a valid item!");
			}

			recipe.ingredient = IngredientJS.fromRecipeJson(json.get("ingredient"));

			if (recipe.ingredient.isEmpty())
			{
				return new RecipeErrorJS("Stonecutting recipe ingredient " + json.get("ingredient") + " is not a valid ingredient!");
			}

			return recipe;
		}
	};

	private IngredientJS ingredient = EmptyItemStackJS.INSTANCE;
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
		json.add("ingredient", ingredient.getJson());
		json.addProperty("result", result.getId().toString());
		json.addProperty("count", result.getCount());
		return json;
	}

	@Override
	public boolean hasInput(IngredientJS i)
	{
		return ingredient.anyStackMatches(i);
	}

	@Override
	public boolean hasOutput(IngredientJS ingredient)
	{
		return ingredient.test(result);
	}
}