package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import dev.latvian.mods.kubejs.recipe.InputReplacement;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.OutputReplacement;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.rhino.type.TypeInfo;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;

public record ArrayRecipeComponent<T>(RecipeComponent<T> component, boolean canWriteSelf, TypeInfo componentTypeInfo, T[] emptyArray) implements RecipeComponent<T[]> {
	@SuppressWarnings("unchecked")
	public ArrayRecipeComponent(RecipeComponent<T> component, boolean canWriteSelf) {
		this(component, canWriteSelf, component.typeInfo(), (T[]) component.typeInfo().newArray(0));
	}

	@Override
	public ComponentRole role() {
		return component.role();
	}

	@Override
	public String componentType() {
		return "array";
	}

	@Override
	public TypeInfo typeInfo() {
		if (canWriteSelf) {
			return componentTypeInfo.or(componentTypeInfo.asArray());
		} else {
			return componentTypeInfo.asArray();
		}
	}

	@SuppressWarnings("unchecked")
	public T[] newArray(int length) {
		if (length == 0) {
			return emptyArray;
		}

		return (T[]) componentTypeInfo.newArray(length);
	}

	@Override
	public boolean hasPriority(KubeRecipe recipe, Object from) {
		return from instanceof Iterable<?> || from != null && from.getClass().isArray();
	}

	@Override
	public JsonElement write(KubeRecipe recipe, T[] value) {
		if (canWriteSelf && value.length == 1) {
			var v1 = component.write(recipe, value[0]);
			return v1 == null ? new JsonArray() : v1;
		}

		var arr = new JsonArray(value.length);

		for (var v : value) {
			var v1 = component.write(recipe, v);

			if (v1 != null) {
				arr.add(v1);
			}
		}

		return arr;
	}

	@Override
	@SuppressWarnings("unchecked")
	public T[] read(KubeRecipe recipe, Object from) {
		if (from.getClass() == emptyArray.getClass()) {
			return (T[]) from;
		} else if (from instanceof Iterable<?> iterable) {
			int size;

			if (iterable instanceof Collection<?> c) {
				size = c.size();
			} else if (iterable instanceof JsonArray a) {
				size = a.size();
			} else {
				size = -1;
			}

			if (size == 0) {
				return emptyArray;
			} else if (size > 0) {
				var arr = newArray(size);

				int i = 0;

				for (var e : iterable) {
					arr[i] = component.read(recipe, e);
					i++;
				}

				return arr;
			} else {
				var list = new ArrayList<T>();

				for (var e : iterable) {
					list.add(component.read(recipe, e));
				}

				return list.toArray(newArray(list.size()));
			}
		} else if (from.getClass().isArray()) {
			int length = Array.getLength(from);

			if (length == 0) {
				return emptyArray;
			}

			var arr = newArray(length);

			for (int i = 0; i < length; i++) {
				arr[i] = component.read(recipe, Array.get(from, i));
			}

			return arr;
		}

		var arr = newArray(1);
		arr[0] = component.read(recipe, from);
		return arr;
	}

	@Override
	public boolean isInput(KubeRecipe recipe, T[] value, ReplacementMatch match) {
		for (var v : value) {
			if (component.isInput(recipe, v, match)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public T[] replaceInput(KubeRecipe recipe, T[] original, ReplacementMatch match, InputReplacement with) {
		var arr = original;

		for (int i = 0; i < original.length; i++) {
			var r = component.replaceInput(recipe, original[i], match, with);

			if (arr[i] != r) {
				if (arr == original) {
					arr = newArray(original.length);
					System.arraycopy(original, 0, arr, 0, i);
				}

				arr[i] = r;
			}
		}

		return arr;
	}

	@Override
	public boolean isOutput(KubeRecipe recipe, T[] value, ReplacementMatch match) {
		for (var v : value) {
			if (component.isOutput(recipe, v, match)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public T[] replaceOutput(KubeRecipe recipe, T[] original, ReplacementMatch match, OutputReplacement with) {
		var arr = original;

		for (int i = 0; i < original.length; i++) {
			var r = component.replaceOutput(recipe, original[i], match, with);

			if (arr[i] != r) {
				if (arr == original) {
					arr = newArray(original.length);
					System.arraycopy(original, 0, arr, 0, i);
				}

				arr[i] = r;
			}
		}

		return arr;
	}

	@Override
	public String toString() {
		return component.toString() + "[]";
	}

	public T[] add(T[] array, T value) {
		var arr = newArray(array.length + 1);
		System.arraycopy(array, 0, arr, 0, array.length);
		arr[array.length] = value;
		return arr;
	}

	public T[] addAll(T[] array, T... values) {
		var arr = newArray(array.length + values.length);
		System.arraycopy(array, 0, arr, 0, array.length);
		System.arraycopy(values, 0, arr, array.length, values.length);
		return arr;
	}

	public T[] remove(T[] array, int index) {
		var arr = newArray(array.length - 1);
		System.arraycopy(array, 0, arr, 0, index);
		System.arraycopy(array, index + 1, arr, index, array.length - index - 1);
		return arr;
	}
}
