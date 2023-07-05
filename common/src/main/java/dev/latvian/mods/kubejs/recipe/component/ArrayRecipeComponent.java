package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import dev.latvian.mods.kubejs.recipe.InputReplacement;
import dev.latvian.mods.kubejs.recipe.OutputReplacement;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.kubejs.typings.desc.DescriptionContext;
import dev.latvian.mods.kubejs.typings.desc.TypeDescJS;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;

public record ArrayRecipeComponent<T>(RecipeComponent<T> component, boolean canWriteSelf, Class<?> arrayClass, T[] emptyArray) implements RecipeComponent<T[]> {
	@Override
	public ComponentRole role() {
		return component.role();
	}

	@Override
	public String componentType() {
		return "array";
	}

	@Override
	public TypeDescJS constructorDescription(DescriptionContext ctx) {
		var d = component.constructorDescription(ctx);

		if (canWriteSelf) {
			return d.or(d.asArray());
		} else {
			return d.asArray();
		}
	}

	@Override
	public Class<?> componentClass() {
		return arrayClass;
	}

	@SuppressWarnings("unchecked")
	public T[] newArray(int length) {
		return length == 0 ? emptyArray : (T[]) Array.newInstance(component.componentClass(), length);
	}

	@Override
	public JsonElement write(RecipeJS recipe, T[] value) {
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
	public T[] read(RecipeJS recipe, Object from) {
		if (from.getClass() == arrayClass) {
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
			var arr = newArray(Array.getLength(from));

			for (int i = 0; i < arr.length; i++) {
				arr[i] = component.read(recipe, Array.get(from, i));
			}

			return arr;
		}

		var arr = newArray(1);
		arr[0] = component.read(recipe, from);
		return arr;
	}

	@Override
	public boolean isInput(RecipeJS recipe, T[] value, ReplacementMatch match) {
		for (var v : value) {
			if (component.isInput(recipe, v, match)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public T[] replaceInput(RecipeJS recipe, T[] original, ReplacementMatch match, InputReplacement with) {
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
	public boolean isOutput(RecipeJS recipe, T[] value, ReplacementMatch match) {
		for (var v : value) {
			if (component.isOutput(recipe, v, match)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public T[] replaceOutput(RecipeJS recipe, T[] original, ReplacementMatch match, OutputReplacement with) {
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
}
