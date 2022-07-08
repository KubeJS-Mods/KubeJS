package dev.latvian.mods.kubejs.recipe;

import java.util.Collections;
import java.util.List;

public record RecipeArguments(RecipeJS recipe, List<?> list) {

	public int size() {
		return list.size();
	}

	public Object get(int index) {
		return index < 0 || index >= list.size() ? null : list.get(index);
	}

	public RecipeArguments list(int index) {
		return get(index) instanceof List<?> l ? new RecipeArguments(recipe, l) : new RecipeArguments(recipe, Collections.emptyList());
	}

	public int getInt(int index, int def) {
		return get(index) instanceof Number n ? n.intValue() : def;
	}

	public float getFloat(int index, float def) {
		return get(index) instanceof Number n ? n.floatValue() : def;
	}

	public double getDouble(int index, double def) {
		return get(index) instanceof Number n ? n.doubleValue() : def;
	}

	public String getString(int index, String def) {
		return get(index) instanceof CharSequence n ? n.toString() : def;
	}
}
