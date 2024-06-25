package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.JSObjectTypeInfo;
import dev.latvian.mods.rhino.type.JSOptionalParam;
import dev.latvian.mods.rhino.type.TypeInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class RecipeComponentBuilder implements RecipeComponent<RecipeComponentBuilderMap> {
	public final List<RecipeKey<?>> keys;
	public Predicate<Set<String>> hasPriority;

	public RecipeComponentBuilder(int init) {
		this.keys = new ArrayList<>(init);
	}

	public RecipeComponentBuilder add(RecipeKey<?> key) {
		keys.add(key);
		return this;
	}

	public RecipeComponentBuilder hasPriority(Predicate<Set<String>> hasPriority) {
		this.hasPriority = hasPriority;
		return this;
	}

	public RecipeComponentBuilder createCopy() {
		var copy = new RecipeComponentBuilder(keys.size());
		copy.keys.addAll(keys);
		copy.hasPriority = hasPriority;
		return copy;
	}

	@Override
	public Codec<RecipeComponentBuilderMap> codec() {
		return new Codec<>() {
			@Override
			public <T> DataResult<Pair<RecipeComponentBuilderMap, T>> decode(DynamicOps<T> ops, T input) {
				/*
				for (var holder : value.holders) {
					holder.key.component.readFromJson(recipe, Cast.to(holder), json);

					if (!holder.key.optional() && holder.value == null) {
						throw new IllegalArgumentException("Missing required key '" + holder.key + "'!");
					}
				}
				 */

				// return ops.getMapEntries(input).flatMap(map -> {
				return DataResult.error(() -> "I don't understand codecs well enough yet");
			}

			@Override
			public <T> DataResult<T> encode(RecipeComponentBuilderMap input, DynamicOps<T> ops, T prefix) {
				var builder = ops.mapBuilder();

				/*
				for (var val : value.holders) {
					if (val.value != null) {
						var vc = new RecipeComponentValue<>(val.key, val.getIndex());
						vc.value = Cast.to(val.value);
						val.key.component.writeToJson(recipe, Cast.to(vc), json);
					}
				}
				 */

				return builder.build(prefix);
			}
		};
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
	public boolean matches(Context cx, KubeRecipe recipe, RecipeComponentBuilderMap value, ReplacementMatchInfo match) {
		for (var e : value.holders) {
			if (e.matches(cx, recipe, match)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public RecipeComponentBuilderMap replace(Context cx, KubeRecipe recipe, RecipeComponentBuilderMap original, ReplacementMatchInfo match, Object with) {
		for (var e : original.holders) {
			if (e.replace(cx, recipe, match, with)) {
				original.hasChanged = true;
			}
		}

		return original;
	}

	@Override
	public String toString() {
		return keys.stream().map(RecipeKey::toString).collect(Collectors.joining(", ", "builder<", ">"));
	}

	@Override
	public boolean checkValueHasChanged(RecipeComponentBuilderMap oldValue, RecipeComponentBuilderMap newValue) {
		return newValue.hasChanged;
	}
}
