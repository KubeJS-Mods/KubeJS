package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.item.EmptyItemError;
import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.recipe.InputReplacement;
import dev.latvian.mods.kubejs.recipe.OutputReplacement;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.kubejs.util.TinyMap;

import java.util.Map;

public record MapRecipeComponent<K, V>(RecipeComponent<K> key, RecipeComponent<V> component, boolean patternKey) implements RecipeComponent<TinyMap<K, V>> {
	public static final RecipeComponent<TinyMap<Character, InputItem>> ITEM_PATTERN_KEY = new MapRecipeComponent<>(StringComponent.CHARACTER, ItemComponents.INPUT, true);

	@Override
	public String componentType() {
		if (patternKey) {
			return component.componentType() + "_pattern_key";
		}

		return "map";
	}

	@Override
	public JsonElement description(RecipeJS recipe) {
		if (patternKey) {
			return RecipeComponent.super.description(recipe);
		}

		var obj = new JsonObject();
		obj.addProperty("type", componentType());
		obj.add("key", key.description(recipe));
		obj.add("component", component.description(recipe));
		return obj;
	}

	@Override
	public ComponentRole role() {
		return component.role();
	}

	@Override
	public Class<?> componentClass() {
		return TinyMap.class;
	}

	@Override
	public JsonObject write(RecipeJS recipe, TinyMap<K, V> value) {
		var json = new JsonObject();

		for (var entry : value.entries()) {
			if (entry.value() != null) {
				json.add(key.write(recipe, entry.key()).getAsString(), component.write(recipe, entry.value()));
			}
		}

		return json;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public TinyMap<K, V> read(RecipeJS recipe, Object from) {
		if (from instanceof TinyMap map) {
			return map;
		} else if (from instanceof JsonObject o) {
			var map = new TinyMap<K, V>(new TinyMap.Entry[o.size()]);
			int i = 0;

			for (var entry : o.entrySet()) {
				var k = key.read(recipe, entry.getKey());

				try {
					var v = component.read(recipe, entry.getValue());
					map.entries()[i++] = new TinyMap.Entry<>(k, v);
				} catch (EmptyItemError ex) {
					if (patternKey) {
						map.entries()[i++] = new TinyMap.Entry<>(k, null);
					} else {
						throw ex;
					}
				}
			}

			return map;
		} else if (from instanceof Map<?, ?> m) {
			var map = new TinyMap<K, V>(new TinyMap.Entry[m.size()]);
			int i = 0;

			for (var entry : m.entrySet()) {
				var k = key.read(recipe, entry.getKey());

				try {
					var v = component.read(recipe, entry.getValue());
					map.entries()[i++] = new TinyMap.Entry<>(k, v);
				} catch (EmptyItemError ex) {
					if (patternKey) {
						map.entries()[i++] = new TinyMap.Entry<>(k, null);
					} else {
						throw ex;
					}
				}
			}

			return map;
		} else {
			throw new IllegalArgumentException("Expected JSON object!");
		}
	}

	@Override
	public boolean isInput(RecipeJS recipe, TinyMap<K, V> value, ReplacementMatch match) {
		for (var entry : value.entries()) {
			if (component.isInput(recipe, entry.value(), match)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public TinyMap<K, V> replaceInput(RecipeJS recipe, TinyMap<K, V> value, ReplacementMatch match, InputReplacement with) {
		var map = value;

		for (int i = 0; i < value.entries().length; i++) {
			var r = component.replaceInput(recipe, value.entries()[i].value(), match, with);

			if (r != value.entries()[i].value()) {
				if (map == value) {
					map = new TinyMap<>(value);
				}

				map.entries()[i] = new TinyMap.Entry<>(value.entries()[i].key(), r);
			}
		}

		return map;
	}

	@Override
	public boolean isOutput(RecipeJS recipe, TinyMap<K, V> value, ReplacementMatch match) {
		for (var entry : value.entries()) {
			if (component.isOutput(recipe, entry.value(), match)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public TinyMap<K, V> replaceOutput(RecipeJS recipe, TinyMap<K, V> value, ReplacementMatch match, OutputReplacement with) {
		var map = value;

		for (int i = 0; i < value.entries().length; i++) {
			var r = component.replaceOutput(recipe, value.entries()[i].value(), match, with);

			if (r != value.entries()[i].value()) {
				if (map == value) {
					map = new TinyMap<>(value);
				}

				map.entries()[i] = new TinyMap.Entry<>(value.entries()[i].key(), r);
			}
		}

		return map;
	}

	@Override
	public String toString() {
		if (patternKey) {
			return componentType();
		}

		return "map{" + key + ":" + component + "}";
	}
}
