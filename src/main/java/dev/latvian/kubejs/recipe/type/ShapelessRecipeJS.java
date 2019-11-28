package dev.latvian.kubejs.recipe.type;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.item.EmptyItemStackJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.recipe.RecipeDeserializerJS;
import dev.latvian.kubejs.recipe.RecipeProviderJS;
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
	public static final RecipeProviderJS PROVIDER = args -> {
		if (args.length == 2)
		{
			Collection<Object> ingredients = UtilsJS.getList(args[1]);

			if (!ingredients.isEmpty())
			{
				ShapelessRecipeJS recipe = new ShapelessRecipeJS();
				recipe.result = ItemStackJS.of(args[0]);

				for (Object o : ingredients)
				{
					IngredientJS in = IngredientJS.of(o);

					if (!in.isEmpty())
					{
						recipe.ingredients.add(in);
					}
				}

				if (!recipe.result.isEmpty() && !recipe.ingredients.isEmpty())
				{
					return recipe;
				}
			}
		}

		return null;
	};

	public static final RecipeDeserializerJS DESERIALIZER = json -> {
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
	};

	public final List<IngredientJS> ingredients = new ArrayList<>();
	public ItemStackJS result = EmptyItemStackJS.INSTANCE;

	@Override
	public IRecipeSerializer getSerializer()
	{
		return IRecipeSerializer.CRAFTING_SHAPELESS;
	}

	@Override
	public JsonObject toJson()
	{
		JsonObject json = create();

		JsonArray ingredientsJson = new JsonArray();

		for (IngredientJS in : ingredients)
		{
			ingredientsJson.add(in.toIngredientJson());
		}

		json.add("ingredients", ingredientsJson);
		json.add("result", result.toRecipeResultJson());
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