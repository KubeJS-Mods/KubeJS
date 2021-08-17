package dev.latvian.kubejs.loot.condition;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.util.ListJS;
import dev.latvian.kubejs.util.MapJS;

import java.util.function.Consumer;

public class AlternativeCondition implements LootCondition, LootConditionImpl {
	public final LootConditionList terms = new LootConditionList();

	public AlternativeCondition(MapJS data) {
		if (!getName().equals(data.get("condition"))) {
			throw new IllegalArgumentException("Cannot create alternative condition by given data");
		}

		Object o = data.get("terms");
		if (!(o instanceof ListJS)) {
			throw new IllegalStateException(String.format("Given condition is corrupt: %s", o));
		}

		((ListJS) o).forEach(element -> {
			if(element instanceof MapJS) {
				terms.customCondition((MapJS) element);
			}
		});
	}

	public AlternativeCondition() {

	}

	@Override
	public JsonObject toJson() {
		JsonObject result = new JsonObject();
		result.addProperty("condition", getName());

		JsonArray termsJsonArray = new JsonArray();
		terms.forEach(lootCondition -> {
			termsJsonArray.add(lootCondition.toJson());
		});

		result.add("terms", termsJsonArray);
		return result;
	}

	@Override
	public String getName() {
		return "minecraft:alternative";
	}

	@Override
	public void modify(Consumer<Object> consumer) {
		consumer.accept(this);
	}

	@Override
	public void clear() {
		terms.clear();
	}

	@Override
	public void handleNewConditionImpl(LootCondition condition) {
		terms.handleNewConditionImpl(condition);
	}
}
