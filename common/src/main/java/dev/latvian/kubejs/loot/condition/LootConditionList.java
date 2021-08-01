package dev.latvian.kubejs.loot.condition;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.rhino.util.HideFromJS;

import java.util.ArrayList;
import java.util.function.Consumer;

public class LootConditionList extends ArrayList<LootCondition> implements LootConditionImpl {
	@HideFromJS
	public void handleNewConditionImpl(LootCondition condition) {
		add(condition);
	}

	@HideFromJS
	public void fill(JsonArray array) {
		if (array == null) {
			return;
		}

		array.forEach(element -> {
			JsonObject jsonObject = element.getAsJsonObject();
			LootCondition condition = LootCondition.of(jsonObject);
			add(condition);
		});
	}

	@HideFromJS
	public void fillJson(JsonObject into) {
		if(isEmpty()) {
			return;
		}

		into.add("conditions", toJson());
	}

	public JsonElement toJson() {
		JsonArray result = new JsonArray();

		forEach(condition -> {
			JsonObject element = condition.toJson();
			result.add(element);
		});

		return result;
	}

	public boolean remove(String s) {
		return removeIf(condition -> s.equals(condition.getName()));
	}

	public void modify(int index, Consumer<LootConditionList> consumer) {
		consumer.accept(this);
	}
}
