package dev.latvian.kubejs.loot.condition;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.loot.AbstractLootElementList;
import dev.latvian.mods.rhino.util.HideFromJS;

public class LootConditionList extends AbstractLootElementList<LootCondition> implements LootConditionImpl {

	@HideFromJS
	public void handleNewConditionImpl(LootCondition condition) {
		elements.add(condition);
	}

	@HideFromJS
	public void addAll(JsonArray array) {
		if (array == null) {
			return;
		}

		array.forEach(element -> {
			JsonObject jsonObject = element.getAsJsonObject();
			LootCondition condition = LootCondition.of(jsonObject);
			elements.add(condition);
		});
	}

	@Override
	protected String getSerializeKey() {
		return "conditions";
	}
}
