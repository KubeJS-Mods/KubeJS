package dev.latvian.kubejs.loot.function;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.rhino.util.HideFromJS;

import java.util.ArrayList;
import java.util.function.Consumer;

public class LootFunctionList extends ArrayList<LootFunction> implements LootFunctionImpl {
	@HideFromJS
	public void fillJson(JsonObject into) {
		if (isEmpty()) {
			return;
		}

		into.add("functions", toJson());
	}

	public JsonElement toJson() {
		JsonArray result = new JsonArray();

		forEach(lf -> {
			JsonObject element = lf.toJson();
			result.add(element);
		});

		return result;
	}

	@HideFromJS
	public void fill(JsonArray array) {
		if (array == null) {
			return;
		}

		array.forEach(element -> {
			JsonObject jsonObject = element.getAsJsonObject();
			LootFunction function = LootFunction.of(jsonObject);
			add(function);
		});
	}

	public LootFunction get(String functionName) {

		return stream()
				.filter(function -> functionName.equals(function.getName()))
				.findFirst()
				.orElse(null);
	}

	@HideFromJS
	public LootFunction handleNewFunctionImpl(LootFunction lootFunction) {
		add(lootFunction);
		return lootFunction;
	}


	public boolean remove(String s) {
		return removeIf(function -> s.equals(function.getName()));
	}

	public void modify(int index, Consumer<LootFunctionList> consumer) {
		consumer.accept(this);
	}
}
