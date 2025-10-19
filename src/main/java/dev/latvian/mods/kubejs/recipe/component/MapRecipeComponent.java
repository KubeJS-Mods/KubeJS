package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.error.RecipeComponentTooLargeException;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.recipe.filter.RecipeMatchContext;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import dev.latvian.mods.kubejs.util.IntBounds;
import dev.latvian.mods.kubejs.util.TinyMap;
import dev.latvian.mods.rhino.type.TypeInfo;

import java.util.Map;

public record MapRecipeComponent<K, V>(RecipeComponent<K> key, RecipeComponent<V> component, boolean patternKey, IntBounds bounds, Codec<TinyMap<K, V>> codec, TypeInfo typeInfo) implements RecipeComponent<TinyMap<K, V>> {
	public static <K, V> MapRecipeComponent<K, V> of(RecipeComponent<K> key, RecipeComponent<V> component, IntBounds bounds) {
		return new MapRecipeComponent<>(key, component, bounds, false);
	}

	public static <V> MapRecipeComponent<Character, V> patternOf(RecipeComponent<V> component, IntBounds bounds) {
		return new MapRecipeComponent<>(CharacterComponent.CHARACTER.instance(), component, bounds, true);
	}

	public static final RecipeComponentType<?> TYPE = RecipeComponentType.<MapRecipeComponent<?, ?>>dynamic(KubeJS.id("map"), (type, ctx) -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		ctx.recipeComponentCodec().fieldOf("key").forGetter(MapRecipeComponent::key),
		ctx.recipeComponentCodec().fieldOf("component").forGetter(MapRecipeComponent::component),
		IntBounds.MAP_CODEC.forGetter(MapRecipeComponent::bounds)
	).apply(instance, MapRecipeComponent::of)));

	public static final RecipeComponentType<?> PATTERN_TYPE = RecipeComponentType.<MapRecipeComponent<?, ?>>dynamic(KubeJS.id("pattern"), (type, ctx) -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		ctx.recipeComponentCodec().fieldOf("component").forGetter(MapRecipeComponent::component),
		IntBounds.MAP_CODEC.forGetter(MapRecipeComponent::bounds)
	).apply(instance, MapRecipeComponent::patternOf)));

	public MapRecipeComponent(RecipeComponent<K> key, RecipeComponent<V> component, IntBounds bounds, boolean patternKey) {
		this(
			key,
			component,
			patternKey,
			bounds,
			Codec.unboundedMap(key.codec(), component.codec()).xmap(TinyMap::ofMap, TinyMap::toMap),
			TypeInfo.RAW_MAP.withParams(key.typeInfo(), component.typeInfo())
		);
	}

	@Override
	public RecipeComponentType<?> type() {
		return patternKey ? PATTERN_TYPE : TYPE;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public TinyMap<K, V> wrap(RecipeScriptContext cx, Object from) {
		return switch (from) {
			case TinyMap map -> map;
			case JsonObject o -> wrap(cx, o.asMap());
			case Map<?, ?> m -> {
				var map = new TinyMap<K, V>(new TinyMap.Entry[m.size()]);
				int i = 0;

				for (var entry : m.entrySet()) {
					var k = key.wrap(cx, entry.getKey());
					var v = component.wrap(cx, entry.getValue());
					map.entries()[i++] = new TinyMap.Entry<>(k, v);
				}

				yield map;
			}
			case null, default -> throw new IllegalArgumentException("Expected JSON object!");
		};
	}

	@Override
	public void validate(RecipeValidationContext ctx, TinyMap<K, V> value) {
		RecipeComponent.super.validate(ctx, value);

		if (value.entries().length > bounds.max()) {
			throw new RecipeComponentTooLargeException(this, value, value.entries().length, bounds.max());
		}

		ctx.errors().push(this);

		for (var entry : value.entries()) {
			ctx.errors().setKey(entry.key());
			component.validate(ctx, entry.value());
		}

		ctx.errors().pop();
	}

	@Override
	public boolean allowEmpty() {
		return bounds.min() <= 0;
	}

	@Override
	public boolean isEmpty(TinyMap<K, V> value) {
		return value.isEmpty();
	}

	@Override
	public boolean matches(RecipeMatchContext cx, TinyMap<K, V> value, ReplacementMatchInfo match) {
		for (var entry : value.entries()) {
			if (component.matches(cx, entry.value(), match)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public TinyMap<K, V> replace(RecipeScriptContext cx, TinyMap<K, V> original, ReplacementMatchInfo match, Object with) {
		var map = original;

		for (int i = 0; i < original.entries().length; i++) {
			var r = component.replace(cx, original.entries()[i].value(), match, with);

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
		} else {
			return "map<" + key + ", " + component + ">";
		}
	}
}
