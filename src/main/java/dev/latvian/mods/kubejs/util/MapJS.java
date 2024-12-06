package dev.latvian.mods.kubejs.util;

import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public interface MapJS {
	@Nullable
	static Map<?, ?> of(@Nullable Object o) {
		if (o instanceof Map<?, ?> m) {
			return m;
		} else if (o instanceof CompoundTag tag) {
			var map = new LinkedHashMap<String, Tag>();

			for (var key : tag.getAllKeys()) {
				map.put(key, tag.get(key));
			}

			return map;
		} else if (o instanceof JsonObject json) {
			var map = new LinkedHashMap<String, Object>();

			for (var entry : json.entrySet()) {
				map.put(entry.getKey(), JsonUtils.toObject(entry.getValue()));
			}

			return map;
		}

		return null;
	}
}