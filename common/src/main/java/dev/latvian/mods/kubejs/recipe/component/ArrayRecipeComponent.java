package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.core.RecipeKJS;
import dev.latvian.mods.kubejs.recipe.InputReplacement;
import dev.latvian.mods.kubejs.recipe.OutputReplacement;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.kubejs.util.MutableBoolean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public record ArrayRecipeComponent<T>(RecipeComponent<T> component, boolean canWriteSelf) implements RecipeComponent<List<T>> {
	@Override
	public RecipeComponentType getType() {
		return component.getType();
	}

	@Override
	public String componentType() {
		return "array";
	}

	@Override
	public JsonObject description() {
		var obj = new JsonObject();
		obj.addProperty("type", componentType());

		if (canWriteSelf) {
			obj.addProperty("can_write_self", true);
		}

		obj.add("component", component.description());
		return obj;
	}

	@Override
	public JsonElement write(List<T> value) {
		if (canWriteSelf && value.size() == 1) {
			var v1 = component.write(value.get(0));
			return v1 == null ? new JsonArray() : v1;
		}

		var arr = new JsonArray(value.size());

		for (var v : value) {
			var v1 = component.write(v);

			if (v1 != null) {
				arr.add(v1);
			}
		}

		return arr;
	}

	@Override
	public List<T> read(Object from) {
		if (from instanceof Iterable<?> arr) {
			var list = new ArrayList<T>(arr instanceof Collection<?> c ? c.size() : arr instanceof JsonArray a ? a.size() : 4);

			for (var e : arr) {
				list.add(component.read(e));
			}

			return list;
		}

		return List.of(component.read(from));
	}

	@Override
	public boolean hasInput(RecipeKJS recipe, List<T> value, ReplacementMatch match) {
		for (var v : value) {
			if (component.hasInput(recipe, v, match)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public List<T> replaceInput(RecipeKJS recipe, List<T> value, ReplacementMatch match, InputReplacement input, MutableBoolean changed) {
		for (int i = 0; i < value.size(); i++) {
			value.set(i, component.replaceInput(recipe, value.get(i), match, input, changed));
		}

		return value;
	}

	@Override
	public boolean hasOutput(RecipeKJS recipe, List<T> value, ReplacementMatch match) {
		for (var v : value) {
			if (component.hasOutput(recipe, v, match)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public List<T> replaceOutput(RecipeKJS recipe, List<T> value, ReplacementMatch match, OutputReplacement with, MutableBoolean changed) {
		for (int i = 0; i < value.size(); i++) {
			value.set(i, component.replaceOutput(recipe, value.get(i), match, with, changed));
		}

		return value;
	}

	@Override
	public String toString() {
		return component.toString() + "[]";
	}
}
