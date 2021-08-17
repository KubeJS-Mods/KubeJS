package dev.latvian.kubejs.loot.condition;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.util.NamedObjectList;
import dev.latvian.mods.rhino.util.HideFromJS;

public class LootConditionList extends NamedObjectList<LootCondition> implements LootConditionImpl {

	@HideFromJS
	public void handleNewConditionImpl(LootCondition condition) {
		add(condition);
	}

	@HideFromJS
	public void addAll(JsonArray array) {
		if (array == null) {
			return;
		}

		array.forEach(element -> {
			JsonObject jsonObject = element.getAsJsonObject();
			LootCondition condition = LootCondition.of(jsonObject);
			add(condition);
		});
	}

	@Override
	protected String getSerializeKey() {
		return "conditions";
	}
}
