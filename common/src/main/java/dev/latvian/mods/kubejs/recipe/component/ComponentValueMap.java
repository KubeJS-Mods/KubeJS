package dev.latvian.mods.kubejs.recipe.component;

import dev.latvian.mods.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.mods.kubejs.recipe.RecipeKey;

import java.util.LinkedHashMap;

public class ComponentValueMap extends LinkedHashMap<RecipeKey<?>, Object> {
	public ComponentValueMap(int init) {
		super(init);
	}

	public <T> T getValue(RecipeKey<T> key) {
		var o = get(key);

		if (o == null) {
			if (key.component() instanceof OptionalRecipeComponent<T> c) {
				return c.defaultValue();
			}

			throw new RecipeExceptionJS("Value for '" + key + "' is missing!");
		}

		return key.component().read(o);
	}
}
