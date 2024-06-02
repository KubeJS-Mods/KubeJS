package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.recipe.InputReplacement;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.OutputReplacement;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.kubejs.recipe.schema.RecipeComponentFactory;
import dev.latvian.mods.kubejs.util.TinyMap;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Map;

public record MapRecipeComponent<K, V>(RecipeComponent<K> key, RecipeComponent<V> component, boolean patternKey) implements RecipeComponent<TinyMap<K, V>> {
	public static final MapRecipeComponent<Character, Ingredient> INGREDIENT_PATTERN_KEY = new MapRecipeComponent<>(StringComponent.CHARACTER, ItemComponents.INPUT, true);

	public static final RecipeComponentFactory FACTORY = RecipeComponentFactory.readTwoComponents((key, component) -> {
		if (key == INGREDIENT_PATTERN_KEY.key && component == INGREDIENT_PATTERN_KEY.component) {
			return INGREDIENT_PATTERN_KEY;
		}

		return new MapRecipeComponent<>(key, component, false);
	});

	@Override
	public Codec<TinyMap<K, V>> codec() {
		return Codec.unboundedMap(key.codec(), component.codec()).xmap(TinyMap::ofMap, TinyMap::toMap);
	}

	@Override
	public TypeInfo typeInfo() {
		return TypeInfo.RAW_MAP.withParams(key.typeInfo(), component.typeInfo());
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public TinyMap<K, V> wrap(Context cx, KubeRecipe recipe, Object from) {
		if (from instanceof TinyMap map) {
			return map;
		} else if (from instanceof JsonObject o) {
			var map = new TinyMap<K, V>(new TinyMap.Entry[o.size()]);
			int i = 0;

			for (var entry : o.entrySet()) {
				var k = key.wrap(cx, recipe, entry.getKey());
				var v = component.wrap(cx, recipe, entry.getValue());
				map.entries()[i++] = new TinyMap.Entry<>(k, v);
			}

			return map;
		} else if (from instanceof Map<?, ?> m) {
			var map = new TinyMap<K, V>(new TinyMap.Entry[m.size()]);
			int i = 0;

			for (var entry : m.entrySet()) {
				var k = key.wrap(cx, recipe, entry.getKey());
				var v = component.wrap(cx, recipe, entry.getValue());
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
	public TinyMap<K, V> replaceInput(Context cx, KubeRecipe recipe, TinyMap<K, V> original, ReplacementMatch match, InputReplacement with) {
		var map = original;

		for (int i = 0; i < original.entries().length; i++) {
			var r = component.replaceInput(cx, recipe, original.entries()[i].value(), match, with);

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
	public TinyMap<K, V> replaceOutput(Context cx, KubeRecipe recipe, TinyMap<K, V> original, ReplacementMatch match, OutputReplacement with) {
		var map = original;

		for (int i = 0; i < original.entries().length; i++) {
			var r = component.replaceOutput(cx, recipe, original.entries()[i].value(), match, with);

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
		return "map<" + key + ", " + component + ">";
	}
}
