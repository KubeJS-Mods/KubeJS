package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.JSObjectTypeInfo;
import dev.latvian.mods.rhino.type.JSOptionalParam;
import dev.latvian.mods.rhino.type.TypeInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RecipeComponentBuilder implements RecipeComponent<Map<RecipeComponentBuilder.Key, RecipeComponentBuilder.Value>> {
	public record Key(String name, RecipeComponent<?> component, boolean optional, boolean alwaysWrite) {
		public Key(String name, RecipeComponent<?> component, boolean optional) {
			this(name, component, optional, false);
		}

		public Key(String name, RecipeComponent<?> component) {
			this(name, component, false);
		}

		@Override
		public String toString() {
			return name + (optional ? "?" : "") + (alwaysWrite ? "!" : "") + ": " + component;
		}
	}

	public record Value(Key key, int index, Object value) implements Map.Entry<Key, Object> {
		@Override
		public Key getKey() {
			return key;
		}

		@Override
		public Object getValue() {
			return value;
		}

		@Override
		public Object setValue(Object object) {
			throw new UnsupportedOperationException();
		}
	}

	public final List<Key> keys;
	public Predicate<Set<String>> hasPriority;

	public RecipeComponentBuilder(List<Key> keys) {
		this.keys = List.copyOf(keys);
	}

	public RecipeComponentBuilder hasPriority(Predicate<Set<String>> hasPriority) {
		this.hasPriority = hasPriority;
		return this;
	}

	public RecipeComponentBuilder createCopy() {
		var copy = new RecipeComponentBuilder(keys);
		copy.hasPriority = hasPriority;
		return copy;
	}

	public MapCodec<Map<Key, Value>> mapCodec() {
		return new MapCodec<>() {
			@Override
			public <T> Stream<T> keys(DynamicOps<T> ops) {
				return keys.stream().map(Key::name).map(ops::createString);
			}

			@Override
			public <T> DataResult<Map<Key, Value>> decode(DynamicOps<T> ops, MapLike<T> input) {
				var map = new HashMap<Key, Value>(keys.size());
				var keyMap = new HashMap<String, Key>();

				keys.forEach(key -> keyMap.put(key.name, key));

				input.entries().forEach(entry -> {
					var key = keyMap.get(ops.getStringValue(entry.getFirst()).getOrThrow());

					if (key != null) {
						map.put(key, new Value(key, keys.indexOf(key), key.component.codec().decode(ops, entry.getSecond())));
					}
				});

				return DataResult.success(map);
			}

			@Override
			public <T> RecordBuilder<T> encode(Map<Key, Value> input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
				var builder = ops.mapBuilder();

				for (var entry : input.values()) {
					builder.add(ops.createString(entry.key.name), entry.key.component.codec().encodeStart(ops, Cast.to(entry.value)));
				}

				return builder;
			}
		};
	}

	@Override
	public Codec<Map<Key, Value>> codec() {
		return mapCodec().codec();
	}

	@Override
	public TypeInfo typeInfo() {
		var list = new ArrayList<JSOptionalParam>(keys.size());

		for (var key : keys) {
			list.add(new JSOptionalParam(key.name, key.component.typeInfo(), key.optional()));
		}

		return new JSObjectTypeInfo(list);
	}

	@Override
	public boolean hasPriority(Context cx, KubeRecipe recipe, Object from) {
		if (from instanceof Map m) {
			if (hasPriority != null) {
				return hasPriority.test(m.keySet());
			} else {
				for (var key : keys) {
					if (!key.optional() && !m.containsKey(key.name)) {
						return false;
					}
				}

				return true;
			}
		} else if (from instanceof JsonObject json) {
			if (hasPriority != null) {
				return hasPriority.test(json.keySet());
			} else {
				for (var key : keys) {
					if (!key.optional() && !json.has(key.name)) {
						return false;
					}
				}

				return true;
			}
		} else {
			return false;
		}
	}

	@Override
	public boolean matches(Context cx, KubeRecipe recipe, Map<Key, Value> value, ReplacementMatchInfo match) {
		for (var e : value.values()) {
			if (e.key.component.matches(cx, recipe, Cast.to(e.value), match)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public Map<Key, Value> replace(Context cx, KubeRecipe recipe, Map<Key, Value> original, ReplacementMatchInfo match, Object with) {
		var replaced = original;

		for (var e : original.values()) {
			var r = e.key.component.replace(cx, recipe, Cast.to(e.value), match, with);

			if (r != e.value) {
				if (replaced == original) {
					replaced = new LinkedHashMap<>(original);
				}

				replaced.put(e.key, new Value(e.key, e.index, r));
			}
		}

		return replaced;
	}

	@Override
	public void buildUniqueId(UniqueIdBuilder builder, Map<Key, Value> map) {
		boolean first = true;

		for (var value : map.values()) {
			if (value.value != null) {
				if (first) {
					first = false;
				} else {
					builder.appendSeparator();
				}

				value.key.component.buildUniqueId(builder, Cast.to(value.value));
			}
		}
	}

	@Override
	public boolean isEmpty(Map<Key, Value> value) {
		return keys.isEmpty();
	}

	@Override
	public String toString() {
		return keys.stream().map(Key::toString).collect(Collectors.joining(", ", "{", "}"));
	}
}
