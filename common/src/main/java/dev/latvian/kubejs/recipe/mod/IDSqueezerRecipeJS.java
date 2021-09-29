package dev.latvian.kubejs.recipe.mod;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.recipe.RecipeJS;
import dev.latvian.kubejs.util.ListJS;

public class IDSqueezerRecipeJS extends RecipeJS {
	@Override
	public void create(ListJS args) {
		outputItems.addAll(parseResultItemList(args.get(0)));
		inputItems.add(parseIngredientItem(args.get(1)));
		json.addProperty("duration", 15);
	}

	@Override
	public void deserialize() {
		JsonObject r = json.get("result").getAsJsonObject();

		if (r.has("fluid")) {
			outputItems.add(ItemStackJS.of(r.get("fluid")));
		}

		if (r.has("items")) {
			outputItems.addAll(parseResultItemList(r.get("items")));
		}

		inputItems.add(parseIngredientItem(json.get("item")));
	}

	@Override
	public void serialize() {
		if (serializeOutputs) {
			JsonObject o = new JsonObject();

			JsonArray a = new JsonArray();

			for (ItemStackJS stack : outputItems) {
				if (stack.getFluidStack() != null) {
					o.add("fluid", stack.toResultJson());
				} else {
					a.add(stack.toResultJson());
				}
			}

			if (a.size() > 0) {
				o.add("items", a);
			}

			json.add("result", o);
		}

		if (serializeInputs) {
			json.add("item", inputItems.get(0).toJson());
		}
	}

	public IDSqueezerRecipeJS duration(int i) {
		json.addProperty("duration", i);
		save();
		return this;
	}
}
