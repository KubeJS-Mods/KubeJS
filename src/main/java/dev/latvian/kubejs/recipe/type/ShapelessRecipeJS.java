package dev.latvian.kubejs.recipe.type;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.item.EmptyItemStackJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.recipe.RecipeTypeJS;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.util.JsonUtilsJS;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.item.crafting.IRecipeSerializer;

import javax.annotation.Nullable;
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
		@Nullable
		@Override
		public RecipeJS create(Object[] args)
		{
			if (args.length != 2)
			{
				ScriptType.SERVER.debugConsole.error("Shapeless recipe requires 2 arguments - result and ingredients!");
				return null;
			}

			ShapelessRecipeJS recipe = new ShapelessRecipeJS();
			recipe.result = ItemStackJS.of(args[0]);

			if (recipe.result.isEmpty())
			{
				ScriptType.SERVER.debugConsole.error("Shapeless recipe result " + JsonUtilsJS.of(args[0]) + " is not a valid item!");
				return null;
			}

			Collection<Object> ingredients = UtilsJS.getList(args[1]);

			if (ingredients.isEmpty())
			{
				ScriptType.SERVER.debugConsole.error("Shapeless recipe requires 2 arguments - result and ingredients!");
				return null;
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
					ScriptType.SERVER.debugConsole.error("Shapeless recipe ingredient " + JsonUtilsJS.of(o) + " is not a valid ingredient!");
					return null;
				}
			}

			return recipe;
		}

		@Nullable
		@Override
		public RecipeJS create(JsonObject json)
		{
			ShapelessRecipeJS recipe = new ShapelessRecipeJS();

			for (JsonElement e : json.get("ingredients").getAsJsonArray())
			{
				IngredientJS i = IngredientJS.fromRecipeJson(e);

				if (!i.isEmpty())
				{
					recipe.ingredients.add(i);
				}
			}

			recipe.result = ItemStackJS.fromRecipeJson(json.get("result"));
			return recipe.result.isEmpty() || recipe.ingredients.isEmpty() ? null : recipe;
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