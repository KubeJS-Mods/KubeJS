package dev.latvian.kubejs.util;

import com.google.gson.JsonObject;
import dev.latvian.kubejs.util.MapJS;
import dev.latvian.mods.rhino.util.HideFromJS;

import java.util.function.Consumer;

/**
 * Stores additional data in a loot table added by mods.
 */
public interface CustomDataOwner {
	JsonObject getCustomData();

	@HideFromJS
	default void setCustomData(JsonObject json) {
		clearCustomData();

		json.entrySet().forEach(entry -> {
			getCustomData().add(entry.getKey(), entry.getValue());
		});
	}

	@HideFromJS
	default void serializeCustomData(JsonObject container) {
		getCustomData().entrySet().forEach(entry -> {
			container.add(entry.getKey(), entry.getValue());
		});
	}

	default void clearCustomData() {
		getCustomData().entrySet().forEach(entry -> {
			getCustomData().remove(entry.getKey());
		});
	}

	default void modifyCustomData(Consumer<MapJS> consumer) {
		MapJS map = MapJS.of(getCustomData());
		if (map == null) {
			return;
		}

		consumer.accept(map);
		setCustomData(map.toJson());
	}
}
