package dev.latvian.mods.kubejs.recipe.mod;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.util.ListJS;
import org.jetbrains.annotations.Nullable;

public class IDSqueezerRecipeJS extends RecipeJS {
	@Override
	public void create(ListJS args) {
		outputItems.addAll(parseResultItemList(args.get(0)));
		inputItems.add(parseIngredientItem(args.get(1)));
		json.addProperty("duration", 40);
	}

	@Override
	public void deserialize() {
		var r = json.get("result").getAsJsonObject();

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
			var o = new JsonObject();

			var a = new JsonArray();

			for (var stack : outputItems) {
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

	@Override
	public ItemStackJS parseResultItem(@Nullable Object o) {
		if (o instanceof JsonObject jsonObj) {
			var e = jsonObj.get("item");

			if (e instanceof JsonObject) {
				var i = super.parseResultItem(e);

				if (jsonObj.has("chance")) {
					i.setChance(jsonObj.get("chance").getAsDouble());
				}

				return i;
			}
		}

		return super.parseResultItem(o);
	}

	@Override
	public JsonElement serializeItemStack(ItemStackJS stack) {
		var o = new JsonObject();
		o.add("item", stack.toRawResultJson());

		if (stack.hasChance()) {
			o.addProperty("chance", stack.getChance());
		}

		return o;
	}
}
