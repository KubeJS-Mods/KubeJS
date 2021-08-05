package dev.latvian.kubejs.loot.function;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.loot.AbstractLootElementList;
import dev.latvian.mods.rhino.util.HideFromJS;

public class LootFunctionList extends AbstractLootElementList<LootFunction> implements LootFunctionImpl {

	@Override
	protected String getSerializeKey() {
		return "functions";
	}

	@HideFromJS
	public void addAll(JsonArray array) {
		if (array == null) {
			return;
		}

		array.forEach(element -> {
			JsonObject jsonObject = element.getAsJsonObject();
			LootFunction function = LootFunction.of(jsonObject);
			elements.add(function);
		});
	}

	@HideFromJS
	public LootFunction handleNewFunctionImpl(LootFunction lootFunction) {
		elements.add(lootFunction);
		return lootFunction;
	}
}
