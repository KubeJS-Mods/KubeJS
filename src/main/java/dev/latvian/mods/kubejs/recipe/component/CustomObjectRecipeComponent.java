package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.kubejs.util.ErrorStack;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.JSObjectTypeInfo;
import dev.latvian.mods.rhino.type.JSOptionalParam;
import dev.latvian.mods.rhino.type.TypeInfo;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CustomObjectRecipeComponent implements RecipeComponent<List<CustomObjectRecipeComponent.Value>> {
	public record Key(String name, RecipeComponent<?> component, boolean optional, boolean alwaysWrite) {
		public static Codec<Key> createCodec(RecipeComponentCodecFactory.Context ctx) {
			return RecordCodecBuilder.create(instance -> instance.group(
				Codec.STRING.fieldOf("name").forGetter(Key::name),
				ctx.codec().fieldOf("component").forGetter(Key::component),
				Codec.BOOL.optionalFieldOf("optional", false).forGetter(Key::optional),
				Codec.BOOL.optionalFieldOf("always_write", false).forGetter(Key::alwaysWrite)
			).apply(instance, Key::new));
		}

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

	public record Value(Key key, int index, Object value) implements Map.Entry<Key, Object>, Comparable<Value> {
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

		@Override
		public int compareTo(@NotNull CustomObjectRecipeComponent.Value value) {
			return Integer.compare(index, value.index);
		}
	}

	public static final RecipeComponentType<List<Value>> TYPE = RecipeComponentType.dynamic(KubeJS.id("custom_object"), (RecipeComponentCodecFactory<CustomObjectRecipeComponent>) ctx -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		Key.createCodec(ctx).listOf().fieldOf("keys").forGetter(CustomObjectRecipeComponent::keys)
	).apply(instance, CustomObjectRecipeComponent::new)));

	private final List<Key> keys;
	public Predicate<Set<String>> hasPriority;
	private Codec<List<Value>> codec;
	private TypeInfo typeInfo;

	public CustomObjectRecipeComponent(List<Key> keys) {
		this.keys = List.copyOf(keys);
	}

	@Override
	public RecipeComponentType<?> type() {
		return TYPE;
	}

	public List<Key> keys() {
		return keys;
	}

	public CustomObjectRecipeComponent hasPriority(Predicate<Set<String>> hasPriority) {
		this.hasPriority = hasPriority;
		return this;
	}

	public CustomObjectRecipeComponent createCopy() {
		var copy = new CustomObjectRecipeComponent(keys);
		copy.hasPriority = hasPriority;
		return copy;
	}

	public MapCodec<List<Value>> mapCodec() {
		return new MapCodec<>() {
			@Override
			public <T> Stream<T> keys(DynamicOps<T> ops) {
				return keys.stream().map(Key::name).map(ops::createString);
			}

			@Override
			public <T> DataResult<List<Value>> decode(DynamicOps<T> ops, MapLike<T> input) {
				var entries = input.entries().toList();
				var list = new ArrayList<Value>(Math.min(keys.size(), entries.size()));
				var keyMap = new HashMap<String, Key>();

				keys.forEach(key -> keyMap.put(key.name, key));

				for (var entry : entries) {
					var key = keyMap.get(ops.getStringValue(entry.getFirst()).getOrThrow());

					if (key != null) {
						list.add(new Value(key, keys.indexOf(key), key.component.codec().decode(ops, entry.getSecond())));
					}
				}

				if (list.size() >= 2) {
					list.sort(null);
				}

				return DataResult.success(list);
			}

			@Override
			public <T> RecordBuilder<T> encode(List<Value> input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
				var builder = ops.mapBuilder();

				for (var entry : input) {
					builder.add(ops.createString(entry.key.name), entry.key.component.codec().encodeStart(ops, Cast.to(entry.value)));
				}

				return builder;
			}
		};
	}

	@Override
	public Codec<List<Value>> codec() {
		if (codec == null) {
			codec = mapCodec().codec();
		}

		return codec;
	}

	@Override
	public TypeInfo typeInfo() {
		if (typeInfo == null) {
			var list = new ArrayList<JSOptionalParam>(keys.size());

			for (var key : keys) {
				list.add(new JSOptionalParam(key.name, key.component.typeInfo(), key.optional()));
			}

			typeInfo = new JSObjectTypeInfo(list);
		}

		return typeInfo;
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
	public boolean matches(Context cx, KubeRecipe recipe, List<Value> value, ReplacementMatchInfo match) {
		for (var e : value) {
			if (e.key.component.matches(cx, recipe, Cast.to(e.value), match)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public List<Value> replace(Context cx, KubeRecipe recipe, List<Value> original, ReplacementMatchInfo match, Object with) {
		var replaced = original;

		for (var e : original) {
			var r = e.key.component.replace(cx, recipe, Cast.to(e.value), match, with);

			if (r != e.value) {
				if (replaced == original) {
					replaced = new ArrayList<>(original);
				}

				replaced.set(e.index, new Value(e.key, e.index, r));
			}
		}

		return replaced;
	}

	@Override
	public void buildUniqueId(UniqueIdBuilder builder, List<Value> list) {
		boolean first = true;

		for (var value : list) {
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
	public void validate(ErrorStack stack, List<Value> value) {
		RecipeComponent.super.validate(stack, value);

		stack.push(this);

		for (var entry : value) {
			stack.setKey(entry.key.name);
			entry.key.component.validate(stack, Cast.to(entry.value));
		}

		stack.pop();
	}

	@Override
	public boolean isEmpty(List<Value> value) {
		return keys.isEmpty();
	}

	@Override
	public String toString() {
		return keys.stream().map(Key::toString).collect(Collectors.joining(", ", "{", "}"));
	}
}
