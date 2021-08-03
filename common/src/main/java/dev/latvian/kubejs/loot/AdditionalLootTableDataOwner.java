package dev.latvian.kubejs.loot;

import com.google.gson.JsonObject;
import dev.latvian.kubejs.util.MapJS;
import dev.latvian.mods.rhino.util.HideFromJS;

import java.util.function.Consumer;

/**
 * Stores additional data in a loot table added by mods.
 */
public interface AdditionalLootTableDataOwner {
	JsonObject getAdditionalData();

	@HideFromJS
	default void setAdditionalData(JsonObject json) {
		clearAdditionalData();

		json.entrySet().forEach(entry -> {
			getAdditionalData().add(entry.getKey(), entry.getValue());
		});
	}

	@HideFromJS
	default void fillAdditionalData(JsonObject container) {
		getAdditionalData().entrySet().forEach(entry -> {
			container.add(entry.getKey(), entry.getValue());
		});
	}

	default void clearAdditionalData() {
		getAdditionalData().entrySet().forEach(entry -> {
			getAdditionalData().remove(entry.getKey());
		});
	}

	default void modifyAdditionalData(Consumer<MapJS> consumer) {
		MapJS map = MapJS.of(getAdditionalData());
		if (map == null) {
			return;
		}

		consumer.accept(map);
		setAdditionalData(map.toJson());
	}
}
