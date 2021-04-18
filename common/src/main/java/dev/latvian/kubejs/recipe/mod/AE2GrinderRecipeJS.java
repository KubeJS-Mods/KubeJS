package dev.latvian.kubejs.recipe.mod;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.recipe.RecipeJS;
import dev.latvian.kubejs.util.ListJS;

/**
 * @author LatvianModder
 */
public class AE2GrinderRecipeJS extends RecipeJS {
	@Override
	public void create(ListJS args) {
		outputItems.add(parseResultItem(args.get(0)));
		inputItems.add(parseIngredientItem(args.get(1)));

		if (args.size() >= 3) {
			outputItems.addAll(parseResultItemList(args.get(2)));
		}

		json.addProperty("turns", args.size() >= 4 ? (Number) args.get(3) : 8);
	}

	@Override
	public void deserialize() {
		JsonObject result = json.get("result").getAsJsonObject();
		outputItems.add(parseResultItem(result.get("primary")));

		if (result.has("optional")) {
			outputItems.addAll(parseResultItemList(result.get("optional")));
		}

		inputItems.add(parseIngredientItem(json.get("input")));
	}

	public AE2GrinderRecipeJS turns(int t) {
		json.addProperty("turns", t);
		save();
		return this;
	}

	@Override
	public void serialize() {
		if (serializeOutputs) {
			JsonObject result = new JsonObject();
			result.add("primary", outputItems.get(0).toResultJson());

			if (outputItems.size() > 1) {
				JsonArray optional = new JsonArray();

				for (int i = 1; i < outputItems.size(); i++) {
					optional.add(outputItems.get(i).toResultJson());
				}

				result.add("optional", optional);
			}

			json.add("result", result);
		}

		if (serializeInputs) {
			json.add("input", inputItems.get(0).toJson());
		}
	}
}