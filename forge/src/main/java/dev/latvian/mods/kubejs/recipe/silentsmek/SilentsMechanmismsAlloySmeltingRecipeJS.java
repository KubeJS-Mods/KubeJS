package dev.latvian.mods.kubejs.recipe.silentsmek;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.kubejs.item.ingredient.MatchAnyIngredientJS;
import dev.latvian.mods.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.util.ListJS;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class SilentsMechanmismsAlloySmeltingRecipeJS extends RecipeJS {
	public List<Integer> inputCount = new ArrayList<>();
	public int processTime = 400;

	@Override
	public void create(ListJS args) {
		ItemStackJS output = ItemStackJS.of(args.get(0));

		if (output.isEmpty()) {
			throw new RecipeExceptionJS("Silents Mechanisms alloy smelting recipe result can't be empty!");
		}

		outputItems.add(output);

		ListJS in = ListJS.orSelf(args.get(1));

		for (Object o : in) {
			IngredientJS i = IngredientJS.of(o);

			if (!i.isEmpty()) {
				int c = i.getCount();

				if (c > 1) {
					inputItems.add(i.withCount(1));
					inputCount.add(c);
				} else {
					inputItems.add(i);
					inputCount.add(1);
				}
			}
		}

		if (inputItems.isEmpty()) {
			throw new RecipeExceptionJS("Silents Mechanisms alloy smelting recipe ingredient " + args.get(1) + " is not a valid ingredient!");
		}

		if (args.size() >= 3 && args.get(2) instanceof Number) {
			processTime(((Number) args.get(2)).intValue());
		}
	}

	private static IngredientJS deserializeIngredient(JsonElement element) {
		if (element.isJsonObject()) {
			JsonObject json = element.getAsJsonObject();

			if (json.has("value")) {
				return IngredientJS.ingredientFromRecipeJson(json.get("value"));
			}
			if (json.has("values")) {
				return IngredientJS.ingredientFromRecipeJson(json.get("values"));
			}
		}

		return IngredientJS.ingredientFromRecipeJson(element);
	}

	@Override
	public void deserialize() {
		ItemStackJS result = ItemStackJS.resultFromRecipeJson(json.get("result"));

		if (result.isEmpty()) {
			throw new RecipeExceptionJS("Silents Mechanisms alloy smelting recipe result can't be empty!");
		}

		processTime = json.has("process_time") ? json.get("process_time").getAsInt() : 400;

		outputItems.add(result);

		for (JsonElement e : json.get("ingredients").getAsJsonArray()) {
			IngredientJS i = deserializeIngredient(e);

			if (!i.isEmpty()) {
				inputItems.add(i);

				if (e.isJsonObject() && e.getAsJsonObject().has("count")) {
					inputCount.add(e.getAsJsonObject().get("count").getAsInt());
				} else {
					inputCount.add(1);
				}
			}
		}

		if (inputItems.isEmpty()) {
			throw new RecipeExceptionJS("Silents Mechanisms alloy smelting recipe ingredient " + json.get("ingredient") + " is not a valid ingredient!");
		}
	}

	@Override
	public void serialize() {
		JsonArray ingredientsJson = new JsonArray();

		for (int i = 0; i < inputItems.size(); i++) {
			JsonObject ingredientJson = new JsonObject();

			JsonArray valuesJson = new JsonArray();

			if (inputItems.get(i) instanceof MatchAnyIngredientJS) {
				for (IngredientJS in : ((MatchAnyIngredientJS) inputItems.get(i)).ingredients) {
					valuesJson.add(in.toJson());
				}
			} else {
				valuesJson.add(inputItems.get(i).toJson());
			}

			ingredientJson.add("value", valuesJson);
			ingredientJson.addProperty("count", inputCount.get(i));
			ingredientsJson.add(ingredientJson);
		}

		json.add("ingredients", ingredientsJson);
		json.add("result", outputItems.get(0).toResultJson());
		json.addProperty("process_time", processTime);
	}

	public SilentsMechanmismsAlloySmeltingRecipeJS processTime(int t) {
		processTime = Math.max(0, t);
		return this;
	}
}