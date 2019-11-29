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
public class StonecuttingRecipeJS extends RecipeJS
{
	public static final RecipeTypeJS TYPE = new RecipeTypeJS(IRecipeSerializer.STONECUTTING)
	{
		@Nullable
		@Override
		public RecipeJS create(Object[] args)
		{
			if (args.length != 2)
			{
				ScriptType.SERVER.debugConsole.error("Stonecutting recipe requires 2 arguments - result and ingredient!");
				return null;
			}

			StonecuttingRecipeJS recipe = new StonecuttingRecipeJS();
			recipe.result = ItemStackJS.of(args[0]);

			if (recipe.result.isEmpty())
			{
				ScriptType.SERVER.debugConsole.error("Stonecutting recipe result " + JsonUtilsJS.of(args[0]) + " is not a valid item!");
				return null;
			}

			recipe.ingredient = IngredientJS.of(args[1]);

			if (recipe.ingredient.isEmpty())
			{
				ScriptType.SERVER.debugConsole.error("Stonecutting recipe ingredient " + JsonUtilsJS.of(args[1]) + " is not a valid ingredient!");
				return null;
			}

			return recipe;
		}

		@Nullable
		@Override
		public RecipeJS create(JsonObject json)
		{
			StonecuttingRecipeJS recipe = new StonecuttingRecipeJS();
			recipe.result = ItemStackJS.fromRecipeJson(json.get("result"));
			recipe.ingredient = IngredientJS.fromRecipeJson(json.get("ingredient"));
			return recipe.result.isEmpty() || recipe.ingredient.isEmpty() ? null : recipe;
		}
	};

	public IngredientJS ingredient = EmptyItemStackJS.INSTANCE;
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