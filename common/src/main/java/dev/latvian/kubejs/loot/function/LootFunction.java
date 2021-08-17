package dev.latvian.kubejs.loot.function;

import com.google.gson.JsonObject;
import dev.latvian.kubejs.loot.condition.LootCondition;
import dev.latvian.kubejs.loot.condition.LootConditionImpl;
import dev.latvian.kubejs.loot.condition.LootConditionList;
import dev.latvian.kubejs.util.MapJS;
import dev.latvian.kubejs.util.NamedObject;
import dev.latvian.mods.rhino.util.HideFromJS;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class LootFunction implements NamedObject, LootConditionImpl {
	public static LootFunction of(@Nullable Object o) {
		MapJS map = MapJS.of(o);
		if (map == null) {
			return null;
		}

		String conditionName = (String) map.get("function");
		if (conditionName == null) {
			throw new IllegalArgumentException("No function name given!");
		}

		return new LootFunction(map);
	}

	public final LootConditionList conditions = new LootConditionList();
	protected final String name;
	protected final MapJS data;

	public LootFunction(String name) {
		this.name = name;
		this.data = new MapJS();
	}

	private LootFunction(MapJS map) {
		name = (String) map.get("function");
		map.remove("function");
		data = map;
	}

	public String getName() {
		return name;
	}

	@HideFromJS
	public void put(String key, Object value) {
		if (key.equals("function")) {
			throw new IllegalArgumentException("Key 'function' is not allowed to be set from outside");
		}

		if (key.equals("conditions")) {
			throw new IllegalArgumentException("Key 'conditions' is not allowed to be set from outside");
		}

		data.put(key, value);
	}

	@HideFromJS
	public void putByKey(String key, MapJS map) {
		if (map.containsKey(key)) {
			put(key, map.get(key));
		}
	}

	public void clear() {
		data.clear();
		conditions.clear();
	}

	public void modify(Consumer<MapJS> consumer) {
		consumer.accept(data);
		data.remove("conditions");
		data.remove("function");
	}

	@Override
	public JsonObject toJson() {
		JsonObject result = new JsonObject();
		result.addProperty("function", getName());
		conditions.serializeInto(result);

		data.toJson().entrySet().forEach(entry -> {
			result.add(entry.getKey(), entry.getValue());
		});

		return result;
	}

	@Override
	public void handleNewConditionImpl(LootCondition condition) {
		conditions.handleNewConditionImpl(condition);
	}
}
