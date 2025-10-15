package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.error.RecipeComponentException;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.recipe.RecipeTypeRegistryContext;
import dev.latvian.mods.kubejs.recipe.filter.RecipeMatchContext;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.rhino.type.JSObjectTypeInfo;
import dev.latvian.mods.rhino.type.JSOptionalParam;
import dev.latvian.mods.rhino.type.TypeInfo;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CustomObjectRecipeComponent implements RecipeComponent<List<CustomObjectRecipeComponent.Value>> {
	public record Key(String name, RecipeComponent<?> component, boolean optional, boolean alwaysWrite) {
		public static Codec<Key> createCodec(RecipeTypeRegistryContext ctx) {
			return RecordCodecBuilder.create(instance -> instance.group(
				Codec.STRING.fieldOf("name").forGetter(Key::name),
				ctx.recipeComponentCodec().fieldOf("component").forGetter(Key::component),
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

	public static final RecipeComponentType<?> TYPE = RecipeComponentType.<CustomObjectRecipeComponent>dynamic(KubeJS.id("custom_object"), (type, ctx) -> RecordCodecBuilder.mapCodec(instance -> instance.group(
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

	@Override
	public List<CustomObjectRecipeComponent.Value> wrap(RecipeScriptContext rcx, Object from) {
		var cx = rcx.cx();

		// already wrapped
		var wrapped = cx.optionalListOf(from, TypeInfo.of(Value.class));
		if (wrapped != null) {
			return Cast.to(wrapped);
		}

		if (cx.isMapLike(from)) {
			Map<Object, Object> map = Objects.requireNonNull(cx.optionalMapOf(from, TypeInfo.NONE, TypeInfo.NONE));

			List<Value> list = new ArrayList<>(keys.size());
			Map<String, Key> keyMap = new HashMap<>();

			keys.forEach(key -> keyMap.put(key.name, key));

			for (var entry : map.entrySet()) {
				var key = switch (entry.getKey()) {
					case Key id -> id;
					case CharSequence cs -> keyMap.get(cs.toString());
					case null -> null;
					default -> keyMap.get(Objects.toString(entry.getKey()));
				};

				if (key == null) {
					throw new IllegalStateException("Unknown key in custom object: " + entry.getKey());
				}

				try {
					var value = Objects.requireNonNull(key.component.wrap(rcx, entry.getValue()), "Wrapped value is null!");

					list.add(new Value(key, keys.indexOf(key), value));
				} catch (Throwable e) {
					throw new RecipeComponentException("Failed to wrap key " + key + " for custom component!", e, this, null, cx.toString(entry.getValue()));
				}
			}

			return list;
		}

		throw new IllegalStateException("Unexpected value: " + from);
	}

	public MapCodec<List<Value>> mapCodec() {
		return new MapCodec<>() {
			@Override
			public <T> Stream<T> keys(DynamicOps<T> ops) {
				return keys.stream().map(Key::name).map(ops::createString);
			}

			@Override
			public <T> DataResult<List<Value>> decode(DynamicOps<T> ops, MapLike<T> input) {
				List<Value> list = new ArrayList<>(keys.size());
				Map<String, Key> keyMap = new HashMap<>();

				keys.forEach(key -> keyMap.put(key.name, key));

				Stream.Builder<Pair<T, T>> failed = Stream.builder();

				var result = input.entries().reduce(
					DataResult.success(Unit.INSTANCE, Lifecycle.stable()),
					(r, pair) -> {
						var keyResult = ops.getStringValue(pair.getFirst()).flatMap(k -> {
							if (keyMap.containsKey(k)) {
								return DataResult.success(keyMap.get(k));
							} else {
								return DataResult.error(() -> "Unknown key in custom object: " + k);
							}
						});

						var valueResult = keyResult.map(k -> k.component.codec())
							.flatMap(codec -> codec.decode(ops, pair.getSecond()))
							.map(Pair::getFirst);

						var entryResult = keyResult.apply2stable((k, v) -> new Value(k, keys.indexOf(k), v), valueResult);

						entryResult.resultOrPartial().ifPresent(list::add);

						if (entryResult.isError()) {
							failed.add(pair);
						}

						return r.apply2stable((u, p) -> u, entryResult);
					},
					(r1, r2) -> r1.apply2stable((u1, u2) -> u1, r2)
				);


				if (list.size() >= 2) {
					list.sort(null);
				}

				var errors = ops.createMap(failed.build());

				return result.map(unit -> list)
					.setPartial(list)
					.mapError(e -> e + " missed input: " + errors);
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
	public boolean hasPriority(RecipeMatchContext cx, Object from) {
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
	public boolean matches(RecipeMatchContext cx, List<Value> value, ReplacementMatchInfo match) {
		for (var e : value) {
			if (e.key.component.matches(cx, Cast.to(e.value), match)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public List<Value> replace(RecipeScriptContext cx, List<Value> original, ReplacementMatchInfo match, Object with) {
		var replaced = original;

		for (var e : original) {
			var r = e.key.component.replace(cx, Cast.to(e.value), match, with);

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
	public void validate(RecipeValidationContext ctx, List<Value> value) {
		RecipeComponent.super.validate(ctx, value);

		ctx.errors().push(this);

		for (var entry : value) {
			ctx.errors().setKey(entry.key.name);
			entry.key.component.validate(ctx, Cast.to(entry.value));
		}

		ctx.errors().pop();
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
