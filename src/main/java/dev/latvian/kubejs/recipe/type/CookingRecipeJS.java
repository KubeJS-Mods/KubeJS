package dev.latvian.kubejs.recipe.type;

import dev.latvian.kubejs.item.EmptyItemStackJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.kubejs.util.ListJS;
import net.minecraft.item.crafting.IRecipeSerializer;

import java.util.Collection;
import java.util.Collections;

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

		public final IRecipeSerializer serializer;

		Type(IRecipeSerializer s)
		{
			serializer = s;
		}
	}

	public final Type cookingType;
	private IngredientJS ingredient = EmptyItemStackJS.INSTANCE;
	private ItemStackJS result = EmptyItemStackJS.INSTANCE;

	public CookingRecipeJS(Type c)
	{
		cookingType = c;
	}

	@Override
	public void create(ListJS args)
	{
		if (args.size() < 2)
		{
			throw new RecipeExceptionJS("Cooking recipe requires at least 2 arguments - result and ingredient!");
		}

		result = ItemStackJS.of(args.get(0));

		if (result.isEmpty())
		{
			throw new RecipeExceptionJS("Cooking recipe result " + args.get(0) + " is not a valid item!");
		}

		ingredient = IngredientJS.of(args.get(1));

		if (ingredient.isEmpty())
		{
			throw new RecipeExceptionJS("Cooking recipe ingredient " + args.get(1) + " is not a valid ingredient!");
		}

		if (args.size() >= 3)
		{
			xp(((Number) args.get(2)).floatValue());
		}

		if (args.size() >= 4)
		{
			cookingTime(((Number) args.get(3)).intValue());
		}
	}

	@Override
	public void deserialize()
	{
		result = ItemStackJS.resultFromRecipeJson(json.get("result"));

		if (result.isEmpty())
		{
			throw new RecipeExceptionJS("Cooking recipe result " + json.get("result") + " is not a valid item!");
		}

		ingredient = IngredientJS.ingredientFromRecipeJson(json.get("ingredient"));

		if (ingredient.isEmpty())
		{
			throw new RecipeExceptionJS("Cooking recipe ingredient " + json.get("ingredient") + " is not a valid ingredient!");
		}
	}

	@Override
	public void serialize()
	{
		json.add("ingredient", ingredient.toJson());
		json.add("result", result.getResultJson());
	}

	public CookingRecipeJS xp(float xp)
	{
		json.addProperty("experience", Math.max(0F, xp));
		return this;
	}

	public CookingRecipeJS cookingTime(int time)
	{
		json.addProperty("cookingtime", Math.max(0, time));
		return this;
	}

	@Override
	public Collection<IngredientJS> getInput()
	{
		return Collections.singleton(ingredient);
	}

	@Override
	public boolean replaceInput(Object i, Object with)
	{
		if (ingredient.anyStackMatches(IngredientJS.of(i)))
		{
			ingredient = IngredientJS.of(with);
			save();
			return true;
		}

		return false;
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