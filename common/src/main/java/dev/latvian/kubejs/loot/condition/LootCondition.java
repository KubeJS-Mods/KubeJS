package dev.latvian.kubejs.loot.condition;

import com.google.gson.JsonObject;
import dev.latvian.kubejs.util.MapJS;
import dev.latvian.kubejs.util.NamedObject;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public interface LootCondition extends NamedObject {
	static LootCondition of(@Nullable Object o) {
		MapJS map = MapJS.of(o);

		if (o instanceof String) {
			map = new MapJS();
			map.put("condition", o);
		}

		if (map == null) {
			return null;
		}

		String conditionName = (String) map.get("condition");
		if(conditionName == null) {
			throw new IllegalArgumentException("No condition name given!");
		}

		switch (conditionName) {
			case "minecraft:inverted":
				return new InvertedCondition(map);
			case "minecraft:alternative":
				return new AlternativeCondition(map);
		}

		return new BasicCondition(map);
	}

	@Override
	JsonObject toJson();

	String getName();

	void modify(Consumer<Object> consumer);

	void clear();
}
