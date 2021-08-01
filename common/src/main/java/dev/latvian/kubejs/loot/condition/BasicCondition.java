package dev.latvian.kubejs.loot.condition;

import com.google.gson.JsonObject;
import dev.latvian.kubejs.util.MapJS;
import dev.latvian.mods.rhino.util.HideFromJS;

import java.util.function.Consumer;

public class BasicCondition implements LootCondition {
	protected MapJS data;

	public BasicCondition(MapJS data) {
		this.data = data;
	}

	public BasicCondition(String name) {
		this(new MapJS());
		data.put("condition", name);
	}

	public void clear() {
		data.entrySet().removeIf(entry -> !entry.getKey().equals("condition"));
	}

	public String getName() {
		return (String) data.get("condition");
	}

	public void modify(Consumer<Object> consumer) {
		MapJS copy = data.copy();
		copy.remove("condition");

		consumer.accept(copy);

		copy.put("condition", getName());
		data = copy;
	}

	@HideFromJS
	public void put(String key, Object value) {
		if (key.equals("condition")) {
			throw new IllegalArgumentException("Key 'condition' is not allowed to be set from outside");
		}
		data.put(key, value);
	}

	@HideFromJS
	public void putByKey(String key, MapJS map) {
		if (map.containsKey(key)) {
			put(key, map.get(key));
		}
	}

	@Override
	public JsonObject toJson() {
		return data.toJson();
	}
}
