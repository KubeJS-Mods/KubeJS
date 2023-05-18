package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.util.UtilsJS;

import java.util.LinkedHashMap;
import java.util.Map;

public class RecipeComponentBuilder implements RecipeComponent<Map<String, Object>> {
	public final Map<String, RecipeKey<?>> map;

	public RecipeComponentBuilder(int init) {
		this.map = new LinkedHashMap<>(init);
	}

	public RecipeComponentBuilder add(RecipeKey<?> key) {
		if (map.put(key.name(), key) != null) {
			throw new IllegalStateException("Component with name '" + key + "' already exists!");
		}

		return this;
	}

	@Override
	public String componentType() {
		return "builder";
	}

	@Override
	public JsonElement write(Map<String, Object> value) {
		var json = new JsonObject();

		for (var entry : value.entrySet()) {
			var k = map.get(entry.getKey());

			if (k != null) {
				var v = k.component().write(UtilsJS.cast(entry.getValue()));

				if (v != null) {
					json.add(entry.getKey(), v);
				}
			} else {
				throw new IllegalStateException("Component with name '" + entry.getKey() + "' does not exist!");
			}
		}

		return json;
	}

	@Override
	public Map<String, Object> read(Object from) {
		if (from instanceof JsonObject o) {
			var m = new LinkedHashMap<String, Object>(o.size());

			for (var entry : o.entrySet()) {
				var k = map.get(entry.getKey());

				if (k != null) {
					m.put(entry.getKey(), k.component().read(entry.getValue()));
				} else {
					throw new IllegalStateException("Component with name '" + entry.getKey() + "' does not exist!");
				}
			}

			return m;
		} else if (from instanceof Map<?, ?> m) {
			var map = new LinkedHashMap<String, Object>(m.size());

			for (var entry : m.entrySet()) {
				var k = this.map.get(String.valueOf(entry.getKey()));

				if (k != null) {
					map.put(entry.getKey().toString(), k.component().read(entry.getValue()));
				} else {
					throw new IllegalStateException("Component with name '" + entry.getKey() + "' does not exist!");
				}
			}

			return map;
		} else {
			throw new IllegalArgumentException("Expected JSON object!");
		}
	}

	public <T> T get(String name, RecipeComponent<T> ignored) {
		return UtilsJS.cast(map.get(name));
	}
}
