package dev.latvian.kubejs.loot.condition;

import com.google.gson.JsonObject;
import dev.latvian.kubejs.util.MapJS;

import java.util.function.Consumer;

public class InvertedCondition implements LootCondition, LootConditionImpl {

	private LootCondition innerCondition;

	public InvertedCondition(MapJS data) {
		if (!getName().equals(data.get("condition"))) {
			throw new IllegalArgumentException("Cannot create inverted condition by given data");
		}

		Object o = data.get("term");
		if (!(o instanceof MapJS)) {
			throw new IllegalStateException(String.format("Given condition is corrupt: %s", o));
		}

		innerCondition = LootCondition.of(o);
	}

	public InvertedCondition() {

	}

	private LootCondition getInnerCondition() {
		if(innerCondition == null) {
			throw new IllegalStateException("The inner condition was not set");
		}

		return innerCondition;
	}

	@Override
	public JsonObject toJson() {
		JsonObject result = new JsonObject();
		result.addProperty("condition", getName());
		result.add("term", getInnerCondition().toJson());
		return result;
	}

	@Override
	public String getName() {
		return "minecraft:inverted";
	}

	@Override
	public void modify(Consumer<Object> consumer) {
		getInnerCondition().modify(consumer);
	}

	@Override
	public void clear() {
		getInnerCondition().clear();
	}

	@Override
	public void handleNewConditionImpl(LootCondition condition) {
		innerCondition = condition;
	}
}
