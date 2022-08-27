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
		if (index < 0 || index >= list.size()) {
			return new RecipeArguments(recipe, Collections.emptyList());
		} else {
			var x = get(index);

			if (x instanceof List<?> l) {
				return new RecipeArguments(recipe, l);
			} else {
				return new RecipeArguments(recipe, Collections.singletonList(x));
			}
		}
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
