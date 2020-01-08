package dev.latvian.kubejs.recipe.type;

import com.google.gson.JsonObject;
import dev.latvian.kubejs.item.EmptyItemStackJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.recipe.RecipeErrorJS;
import dev.latvian.kubejs.recipe.RecipeTypeJS;
import dev.latvian.kubejs.util.ListJS;
import net.minecraft.item.crafting.IRecipeSerializer;

/**
 * @author LatvianModder
 */
public class CookingRecipeJS extends RecipeJS
{
	public enum Type
	{
		SMELTING(IRecipeSerializer.SMELTING),
		BLASTING(IRecipeSerializer.BLASTING),
		SMOKING(IRecipeSerializer.SMOKING),
		CAMPFIRE(IRecipeSerializer.CAMPFIRE_COOKING);

		public final RecipeTypeJS type;

		Type(IRecipeSerializer s)
		{
			type = new RecipeTypeJS(s)
			{
				@Override
				public RecipeJS create(ListJS args)
				{
					if (args.size() != 2)
					{
						return new RecipeErrorJS("Cooking recipe requires 2 arguments - result and ingredient!");
					}

					CookingRecipeJS recipe = new CookingRecipeJS(Type.this);
					recipe.result = ItemStackJS.of(args.get(0));

					if (recipe.result.isEmpty())
					{
						return new RecipeErrorJS("Cooking recipe result " + args.get(0) + " is not a valid item!");
					}

					recipe.ingredient = IngredientJS.of(args.get(1));

					if (recipe.ingredient.isEmpty())
					{
						return new RecipeErrorJS("Cooking recipe ingredient " + args.get(1) + " is not a valid ingredient!");
					}

					return recipe;
				}

				@Override
				public RecipeJS create(JsonObject json)
				{
					CookingRecipeJS recipe = new CookingRecipeJS(Type.this);
					recipe.result = ItemStackJS.resultFromRecipeJson(json.get("result"));

					if (recipe.result.isEmpty())
					{
						return new RecipeErrorJS("Cooking recipe result " + json.get("result") + " is not a valid item!");
					}

					recipe.ingredient = IngredientJS.ingredientFromRecipeJson(json.get("ingredient"));

					if (recipe.ingredient.isEmpty())
					{
						return new RecipeErrorJS("Cooking recipe ingredient " + json.get("ingredient") + " is not a valid ingredient!");
					}

					return recipe;
				}
			};
		}
	}

	private final Type cookingType;
	private IngredientJS ingredient = EmptyItemStackJS.INSTANCE;
	private ItemStackJS result = EmptyItemStackJS.INSTANCE;
	private float experience = 0.1F;

	public CookingRecipeJS(Type c)
	{
		cookingType = c;
	}

	@Override
	public RecipeTypeJS getType()
	{
		return cookingType.type;
	}

	@Override
	public JsonObject toJson()
	{
		JsonObject json = create();
		json.add("ingredient", ingredient.toJson());
		json.add("result", result.getResultJson());
		json.addProperty("experience", experience);
		return json;
	}

	public CookingRecipeJS xp(float xp)
	{
		experience = xp;
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
		return IngredientJS.of(i).test(result);
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
		if (IngredientJS.of(i).test(result))
		{
			result = ItemStackJS.of(with);
			return true;
		}

		return false;
	}
}