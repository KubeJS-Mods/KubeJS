package dev.latvian.kubejs.recipe.type;

import com.google.gson.JsonObject;
import dev.latvian.kubejs.item.EmptyItemStackJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.recipe.RecipeTypeJS;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.util.JsonUtilsJS;
import net.minecraft.item.crafting.IRecipeSerializer;

import javax.annotation.Nullable;

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
				@Nullable
				@Override
				public RecipeJS create(Object[] args)
				{
					if (args.length != 2)
					{
						ScriptType.SERVER.debugConsole.error("Cooking recipe requires 2 arguments - result and ingredient!");
						return null;
					}

					CookingRecipeJS recipe = new CookingRecipeJS(Type.this);
					recipe.result = ItemStackJS.of(args[0]);

					if (recipe.result.isEmpty())
					{
						ScriptType.SERVER.debugConsole.error("Cooking recipe result " + JsonUtilsJS.of(args[0]) + " is not a valid item!");
						return null;
					}

					recipe.ingredient = IngredientJS.of(args[1]);

					if (recipe.ingredient.isEmpty())
					{
						ScriptType.SERVER.debugConsole.error("Cooking recipe ingredient " + JsonUtilsJS.of(args[1]) + " is not a valid ingredient!");
						return null;
					}

					return recipe;
				}

				@Nullable
				@Override
				public RecipeJS create(JsonObject json)
				{
					CookingRecipeJS recipe = new CookingRecipeJS(Type.this);
					recipe.result = ItemStackJS.fromRecipeJson(json.get("result"));
					recipe.ingredient = IngredientJS.fromRecipeJson(json.get("ingredient"));
					return recipe.result.isEmpty() || recipe.ingredient.isEmpty() ? null : recipe;
				}
			};
		}
	}

	public final Type cookingType;
	public IngredientJS ingredient = EmptyItemStackJS.INSTANCE;
	public ItemStackJS result = EmptyItemStackJS.INSTANCE;
	public float experience = 0.1F;

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
		json.add("ingredient", ingredient.getJson());
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