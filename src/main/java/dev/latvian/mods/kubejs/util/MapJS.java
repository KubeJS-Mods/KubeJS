package dev.latvian.mods.kubejs.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.latvian.mods.rhino.Context;
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

	static Map<?, ?> orEmpty(@Nullable Object o) {
		var map = of(o);
		return map != null ? map : Map.of();
	}

	@Nullable
	@Deprecated
	static CompoundTag nbt(Context cx, @Nullable Object map) {
		return NBTUtils.toTagCompound(cx, map);
	}

	@Nullable
	static JsonObject json(Context cx, @Nullable Object map) {
		if (map instanceof JsonObject json) {
			return json;
		} else if (map instanceof CharSequence) {
			try {
				return JsonUtils.GSON.fromJson(map.toString(), JsonObject.class);
			} catch (Exception ex) {
				return null;
			}
		}

		var m = of(map);

		if (m != null) {
			var json = new JsonObject();

			for (var entry : m.entrySet()) {
				var e = JsonUtils.of(cx, entry.getValue());

				if (e instanceof JsonPrimitive p && p.isNumber() && p.getAsNumber() instanceof Double d && d <= Long.MAX_VALUE && d >= Long.MIN_VALUE && d == d.longValue()) {
					json.add(String.valueOf(entry.getKey()), new JsonPrimitive(d.longValue()));
				} else {
					json.add(String.valueOf(entry.getKey()), e);
				}
			}

			return json;
		}

		return null;
	}
}