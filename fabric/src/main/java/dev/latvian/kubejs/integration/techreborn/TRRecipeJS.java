package dev.latvian.kubejs.integration.techreborn;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.item.ingredient.IngredientStackJS;
import dev.latvian.kubejs.recipe.RecipeJS;
import dev.latvian.kubejs.util.ListJS;

/**
 * @author LatvianModder
 */
public class TRRecipeJS extends RecipeJS
{
	@Override
	public void create(ListJS args)
	{
		outputItems.addAll(parseResultItemList(args.get(0)));
		inputItems.addAll(parseIngredientItemList(args.get(1)));
		json.addProperty("power", args.size() >= 3 ? ((Number) args.get(2)).intValue() : 2);
		json.addProperty("time", args.size() >= 4 ? ((Number) args.get(3)).intValue() : 200);
	}

	@Override
	public void deserialize()
	{
		outputItems.addAll(parseResultItemList(json.get("results")));
		inputItems.addAll(parseIngredientItemList(json.get("ingredients")));
	}

	public TRRecipeJS power(int i)
	{
		json.addProperty("power", i);
		save();
		return this;
	}

	public TRRecipeJS time(int i)
	{
		json.addProperty("time", i);
		save();
		return this;
	}

	@Override
	public void serialize()
	{
		if (serializeOutputs)
		{
			JsonArray array = new JsonArray();

			for (ItemStackJS out : outputItems)
			{
				array.add(out.toResultJson());
			}

			json.add("results", array);
		}

		if (serializeInputs)
		{
			JsonArray array = new JsonArray();

			for (IngredientJS in : inputItems)
			{
				array.add(in.toJson());
			}

			json.add("ingredients", array);
		}
	}

	@Override
	public JsonElement serializeIngredientStack(IngredientStackJS in)
	{
		JsonObject o = in.ingredient.toJson().getAsJsonObject();
		o.addProperty("count", in.getCount());
		return o;
	}
}
