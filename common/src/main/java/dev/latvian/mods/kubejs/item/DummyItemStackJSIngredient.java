package dev.latvian.mods.kubejs.item;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientStackJS;

/**
 * @author LatvianModder
 */
public class DummyItemStackJSIngredient implements IngredientJS {
	public final ItemStackJS itemStack;

	DummyItemStackJSIngredient(ItemStackJS i) {
		itemStack = i;
	}

	@Override
	public boolean test(ItemStackJS stack) {
		return false;
	}

	@Override
	public IngredientStackJS asIngredientStack() {
		return new IngredientStackJS(this, itemStack.getCount());
	}

	@Override
	public JsonElement toJson() {
		if (itemStack.isEmpty()) {
			return new JsonArray();
		}

		var json = new JsonObject();
		json.addProperty("item", itemStack.getId());

		if (itemStack.hasNBT()) {
			json.addProperty("type", "forge:nbt");
			json.addProperty("nbt", itemStack.getNbtString());
		}

		return json;
	}
}
