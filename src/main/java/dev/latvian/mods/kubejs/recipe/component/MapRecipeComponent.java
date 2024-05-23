package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.recipe.InputReplacement;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.OutputReplacement;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.kubejs.typings.desc.DescriptionContext;
import dev.latvian.mods.kubejs.typings.desc.TypeDescJS;
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
	public TypeDescJS constructorDescription(DescriptionContext ctx) {
		return component.constructorDescription(ctx).asMap(key.constructorDescription(ctx));
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
	public JsonObject write(KubeRecipe recipe, TinyMap<K, V> value) {
		var json = new JsonObject();

		for (var entry : value.entries()) {
			json.add(key.write(recipe, entry.key()).getAsString(), component.write(recipe, entry.value()));
		}

		return json;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public TinyMap<K, V> read(KubeRecipe recipe, Object from) {
		if (from instanceof TinyMap map) {
			return map;
		} else if (from instanceof JsonObject o) {
			var map = new TinyMap<K, V>(new TinyMap.Entry[o.size()]);
			int i = 0;

			for (var entry : o.entrySet()) {
				var k = key.read(recipe, entry.getKey());
				var v = component.read(recipe, entry.getValue());
				map.entries()[i++] = new TinyMap.Entry<>(k, v);
			}

			return map;
		} else if (from instanceof Map<?, ?> m) {
			var map = new TinyMap<K, V>(new TinyMap.Entry[m.size()]);
			int i = 0;

			for (var entry : m.entrySet()) {
				var k = key.read(recipe, entry.getKey());
				var v = component.read(recipe, entry.getValue());
				map.entries()[i++] = new TinyMap.Entry<>(k, v);
			}

			return map;
		} else {
			throw new IllegalArgumentException("Expected JSON object!");
		}
	}

	@Override
	public String checkEmpty(RecipeKey<TinyMap<K, V>> key, TinyMap<K, V> value) {
		if (value.isEmpty()) {
			return "Map '" + key.name + "' can't be empty!";
		}

		return "";
	}

	@Override
	public boolean isInput(KubeRecipe recipe, TinyMap<K, V> value, ReplacementMatch match) {
		for (var entry : value.entries()) {
			if (component.isInput(recipe, entry.value(), match)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public TinyMap<K, V> replaceInput(KubeRecipe recipe, TinyMap<K, V> original, ReplacementMatch match, InputReplacement with) {
		var map = original;

		for (int i = 0; i < original.entries().length; i++) {
			var r = component.replaceInput(recipe, original.entries()[i].value(), match, with);

			if (r != original.entries()[i].value()) {
				if (map == original) {
					map = new TinyMap<>(original);
				}

				map.entries()[i] = new TinyMap.Entry<>(original.entries()[i].key(), r);
			}
		}

		return map;
	}

	@Override
	public boolean isOutput(KubeRecipe recipe, TinyMap<K, V> value, ReplacementMatch match) {
		for (var entry : value.entries()) {
			if (component.isOutput(recipe, entry.value(), match)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public TinyMap<K, V> replaceOutput(KubeRecipe recipe, TinyMap<K, V> original, ReplacementMatch match, OutputReplacement with) {
		var map = original;

		for (int i = 0; i < original.entries().length; i++) {
			var r = component.replaceOutput(recipe, original.entries()[i].value(), match, with);

			if (r != original.entries()[i].value()) {
				if (map == original) {
					map = new TinyMap<>(original);
				}

				map.entries()[i] = new TinyMap.Entry<>(original.entries()[i].key(), r);
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
