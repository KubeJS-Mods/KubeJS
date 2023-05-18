package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonObject;

import java.util.LinkedHashMap;
import java.util.Map;

public record PatternKeyRecipeComponent<T>(RecipeComponent<T> component) implements RecipeComponent<Map<Character, T>> {
	@Override
	public String componentType() {
		return "pattern_key";
	}

	@Override
	public JsonObject description() {
		var obj = new JsonObject();
		obj.addProperty("type", componentType());
		obj.add("component", component.description());
		return obj;
	}

	@Override
	public RecipeComponentType getType() {
		return component.getType();
	}

	@Override
	public JsonObject write(Map<Character, T> value) {
		var json = new JsonObject();

		for (var entry : value.entrySet()) {
			json.add(entry.getKey().toString(), component.write(entry.getValue()));
		}

		return json;
	}

	@Override
	public Map<Character, T> read(Object from) {
		if (from instanceof JsonObject o) {
			var map = new LinkedHashMap<Character, T>(o.size());

			for (var entry : o.entrySet()) {
				map.put(entry.getKey().charAt(0), component.read(entry.getValue()));
			}

			return map;
		} else if (from instanceof Map<?, ?> m) {
			var map = new LinkedHashMap<Character, T>(m.size());

			for (var entry : m.entrySet()) {
				map.put(entry.getKey() instanceof Character c ? c : entry.getKey().toString().charAt(0), component.read(entry.getValue()));
			}

			return map;
		} else {
			throw new IllegalArgumentException("Expected JSON object!");
		}
	}
}
