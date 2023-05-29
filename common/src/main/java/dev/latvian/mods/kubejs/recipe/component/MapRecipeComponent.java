package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.core.RecipeKJS;
import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.recipe.InputReplacement;
import dev.latvian.mods.kubejs.recipe.OutputReplacement;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.kubejs.util.MutableBoolean;

import java.util.LinkedHashMap;
import java.util.Map;

public record MapRecipeComponent<K, V>(RecipeComponent<K> key, RecipeComponent<V> component) implements RecipeComponent<Map<K, V>> {
	public static final RecipeComponent<Map<Character, InputItem>> PATTERN_KEY = new MapRecipeComponent<>(StringComponent.CHARACTER, ItemComponents.INPUT);

	@Override
	public String componentType() {
		if (this == PATTERN_KEY) {
			return "pattern_key";
		}

		return "map";
	}

	@Override
	public JsonObject description() {
		var obj = new JsonObject();

		if (this == PATTERN_KEY) {
			obj.addProperty("type", componentType());
			return obj;
		}

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
	public JsonObject write(Map<K, V> value) {
		var json = new JsonObject();

		for (var entry : value.entrySet()) {
			json.add(entry.getKey().toString(), component.write(entry.getValue()));
		}

		return json;
	}

	@Override
	public Map<K, V> read(Object from) {
		if (from instanceof JsonObject o) {
			var map = new LinkedHashMap<K, V>(o.size());

			for (var entry : o.entrySet()) {
				var k = key.read(entry.getKey());
				var v = component.read(entry.getValue());
				map.put(k, v);
			}

			return map;
		} else if (from instanceof Map<?, ?> m) {
			var map = new LinkedHashMap<K, V>(m.size());

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

	@Override
	public boolean hasInput(RecipeKJS recipe, Map<K, V> value, ReplacementMatch match) {
		for (var entry : value.entrySet()) {
			if (component.hasInput(recipe, entry.getValue(), match)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public Map<K, V> replaceInput(RecipeKJS recipe, Map<K, V> value, ReplacementMatch match, InputReplacement with, MutableBoolean changed) {
		var map = value;

		for (var entry : value.entrySet()) {
			if (component.hasInput(recipe, entry.getValue(), match)) {
				if (map == value) {
					changed.value = true;
					map = new LinkedHashMap<>(value);
				}

				map.put(entry.getKey(), with.replaceInput(recipe, match, entry.getValue()));
			}
		}

		return map;
	}

	@Override
	public boolean hasOutput(RecipeKJS recipe, Map<K, V> value, ReplacementMatch match) {
		for (var entry : value.entrySet()) {
			if (component.hasOutput(recipe, entry.getValue(), match)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public Map<K, V> replaceOutput(RecipeKJS recipe, Map<K, V> value, ReplacementMatch match, OutputReplacement with, MutableBoolean changed) {
		var map = value;

		for (var entry : value.entrySet()) {
			if (component.hasInput(recipe, entry.getValue(), match)) {
				if (map == value) {
					changed.value = true;
					map = new LinkedHashMap<>(value);
				}

				map.put(entry.getKey(), with.replaceOutput(recipe, match, entry.getValue()));
			}
		}

		return map;
	}

	@Override
	public String toString() {
		if (this == PATTERN_KEY) {
			return componentType();
		}

		return "map{" + key + ":" + component + "}";
	}
}
