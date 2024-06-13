package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonArray;
import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.KubeJSCodecs;
import dev.latvian.mods.kubejs.recipe.InputReplacement;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.OutputReplacement;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public record ListRecipeComponent<T>(RecipeComponent<T> component, boolean canWriteSelf, TypeInfo listTypeInfo, Codec<List<T>> listCodec) implements RecipeComponent<List<T>> {
	static <L> ListRecipeComponent<L> create(RecipeComponent<L> component, boolean canWriteSelf) {
		var typeInfo = component.typeInfo();
		var codec = component.codec();

		if (canWriteSelf) {
			return new ListRecipeComponent<>(component, true, TypeInfo.RAW_LIST.withParams(typeInfo).or(typeInfo), KubeJSCodecs.listOfOrSelf(codec));
		} else {
			return new ListRecipeComponent<>(component, false, TypeInfo.RAW_LIST.withParams(typeInfo), codec.listOf());
		}
	}

	@Override
	public Codec<List<T>> codec() {
		return listCodec;
	}

	@Override
	public TypeInfo typeInfo() {
		return listTypeInfo;
	}

	@Override
	public boolean hasPriority(Context cx, KubeRecipe recipe, Object from) {
		return from instanceof Iterable<?> || from != null && from.getClass().isArray();
	}

	@Override
	public List<T> wrap(Context cx, KubeRecipe recipe, Object from) {
		if (from instanceof Iterable<?> iterable) {
			int size;

			if (iterable instanceof Collection<?> c) {
				size = c.size();
			} else if (iterable instanceof JsonArray a) {
				size = a.size();
			} else {
				size = -1;
			}

			if (size == 0) {
				return List.of();
			} else if (size == 1) {
				return List.of(component.wrap(cx, recipe, iterable.iterator().next()));
			} else if (size == 2) {
				return List.of(component.wrap(cx, recipe, iterable.iterator().next()), component.wrap(cx, recipe, iterable.iterator().next()));
			} else if (size > 0) {
				var arr = new ArrayList<T>(size);

				for (var e : iterable) {
					arr.add(component.wrap(cx, recipe, e));
				}

				return arr;
			} else {
				var list = new ArrayList<T>();

				for (var e : iterable) {
					list.add(component.wrap(cx, recipe, e));
				}

				return list;
			}
		} else if (from.getClass().isArray()) {
			int length = Array.getLength(from);

			if (length == 0) {
				return List.of();
			}

			var arr = new ArrayList<T>(length);

			for (int i = 0; i < length; i++) {
				arr.add(component.wrap(cx, recipe, Array.get(from, i)));
			}

			return arr;
		}

		return List.of(component.wrap(cx, recipe, from));
	}

	@Override
	public boolean matches(KubeRecipe recipe, List<T> value, ReplacementMatch match) {
		for (var v : value) {
			if (component.matches(recipe, v, match)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public List<T> replaceInput(Context cx, KubeRecipe recipe, List<T> original, ReplacementMatch match, InputReplacement with) {
		var arr = original;

		for (int i = 0; i < original.size(); i++) {
			var r = component.replaceInput(cx, recipe, original.get(i), match, with);

			if (arr.get(i) != r) {
				if (arr == original) {
					arr = new ArrayList<>(original);
				}

				if (arr != original) {
					arr.set(i, r);
				}
			}
		}

		return arr;
	}

	@Override
	public List<T> replaceOutput(Context cx, KubeRecipe recipe, List<T> original, ReplacementMatch match, OutputReplacement with) {
		var arr = original;

		for (int i = 0; i < original.size(); i++) {
			var r = component.replaceOutput(cx, recipe, original.get(i), match, with);

			if (arr.get(i) != r) {
				if (arr == original) {
					arr = new ArrayList<>(original);
				}

				if (arr != original) {
					arr.set(i, r);
				}
			}
		}

		return arr;
	}

	@Override
	@Nullable
	public String createUniqueId(List<T> value) {
		if (value == null || value.isEmpty()) {
			return null;
		}

		var sb = new StringBuilder();

		for (var item : value) {
			var u = component.createUniqueId(item);

			if (u != null) {
				if (!sb.isEmpty()) {
					sb.append('_');
				}

				sb.append(u);
			}
		}

		return sb.isEmpty() ? null : sb.toString();
	}

	@Override
	public String toString() {
		return component + (canWriteSelf ? "[?]" : "[]");
	}
}
