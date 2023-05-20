package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonObject;

import java.util.LinkedHashMap;
import java.util.Map;

public record MapRecipeComponent<K, T>(RecipeComponent<K> key, RecipeComponent<T> component) implements RecipeComponent<Map<K, T>> {
	@Override
	public String componentType() {
		return "map";
	}

	@Override
	public JsonObject description() {
		var obj = new JsonObject();
		obj.addProperty("type", componentType());
		obj.add("key", key.description());
		obj.add("component", component.description());
		return obj;
	}

	@Override
	public RecipeComponentType getType() {
		return component.getType();
	}

	@Override
	public JsonObject write(Map<K, T> value) {
		var json = new JsonObject();

		for (var entry : value.entrySet()) {
			json.add(entry.getKey().toString(), component.write(entry.getValue()));
		}

		return json;
	}

	@Override
	public Map<K, T> read(Object from) {
		if (from instanceof JsonObject o) {
			var map = new LinkedHashMap<K, T>(o.size());

			for (var entry : o.entrySet()) {
				var k = key.read(entry.getKey());
				var v = component.read(entry.getValue());
				map.put(k, v);
			}

			return map;
		} else if (from instanceof Map<?, ?> m) {
			var map = new LinkedHashMap<K, T>(m.size());

			for (var entry : m.entrySet()) {
				var k = key.read(entry.getKey());
				var v = component.read(entry.getValue());
				map.put(k, v);
			}

			return map;
		} else {
			throw new IllegalArgumentException("Expected JSON object!");
		}
	}
}
