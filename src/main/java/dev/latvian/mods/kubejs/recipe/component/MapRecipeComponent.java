package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.error.EmptyRecipeComponentException;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import dev.latvian.mods.kubejs.recipe.schema.RecipeComponentFactory;
import dev.latvian.mods.kubejs.util.TinyMap;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Map;

public record MapRecipeComponent<K, V>(RecipeComponent<K> key, RecipeComponent<V> component, boolean patternKey) implements RecipeComponent<TinyMap<K, V>> {
	public static final MapRecipeComponent<Character, Ingredient> INGREDIENT_PATTERN_KEY = new MapRecipeComponent<>(CharacterComponent.CHARACTER, IngredientComponent.INGREDIENT, true);

	public static final RecipeComponentFactory FACTORY = RecipeComponentFactory.readTwoComponents((key, component) -> {
		if (key == CharacterComponent.CHARACTER && component == INGREDIENT_PATTERN_KEY.component) {
			return INGREDIENT_PATTERN_KEY;
		}

		return new MapRecipeComponent<>(key, component, false);
	});

	public static final RecipeComponentFactory PATTERN_FACTORY = RecipeComponentFactory.readOneComponent(component -> {
		if (component == INGREDIENT_PATTERN_KEY.component) {
			return INGREDIENT_PATTERN_KEY;
		}

		return new MapRecipeComponent<>(CharacterComponent.CHARACTER, component, true);
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
	public void validate(TinyMap<K, V> value) {
		if (value.isEmpty()) {
			throw new EmptyRecipeComponentException(this);
		}

		for (var entry : value.entries()) {
			component.validate(entry.value());
		}
	}

	@Override
	public boolean isEmpty(TinyMap<K, V> value) {
		if (value.isEmpty()) {
			return true;
		}

		for (var entry : value.entries()) {
			if (component.isEmpty(entry.value())) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean matches(Context cx, KubeRecipe recipe, TinyMap<K, V> value, ReplacementMatchInfo match) {
		for (var entry : value.entries()) {
			if (component.matches(cx, recipe, entry.value(), match)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public TinyMap<K, V> replace(Context cx, KubeRecipe recipe, TinyMap<K, V> original, ReplacementMatchInfo match, Object with) {
		var map = original;

		for (int i = 0; i < original.entries().length; i++) {
			var r = component.replace(cx, recipe, original.entries()[i].value(), match, with);

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
	public void buildUniqueId(UniqueIdBuilder builder, TinyMap<K, V> value) {
		boolean first = true;

		for (var entry : value.entries()) {
			if (entry.value() != null) {
				if (first) {
					first = false;
				} else {
					builder.appendSeparator();
				}

				component.buildUniqueId(builder, entry.value());
			}
		}
	}

	@Override
	public String toString() {
		if (patternKey) {
			return "pattern<" + component + ">";
		}

		return "map<" + key + ", " + component + ">";
	}
}
