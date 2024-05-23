package dev.latvian.mods.kubejs.recipe.component;

import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.mods.kubejs.recipe.RecipeKey;

import java.util.IdentityHashMap;

public class ComponentValueMap extends IdentityHashMap<RecipeKey<?>, Object> {
	public ComponentValueMap(int init) {
		super(init);
	}

	public <T> T getValue(KubeRecipe recipe, RecipeKey<T> key) {
		var o = get(key);

		if (o == null) {
			if (key.optional()) {
				return null;
			}

			throw new RecipeExceptionJS("Value for '" + key + "' is missing!");
		}

		try {
			return key.component.read(recipe, o);
		} catch (Throwable ex) {
			throw new RecipeExceptionJS("Unable to cast '" + key + "' value '" + o + "' to '" + key.component.componentType() + "'!", ex);
		}
	}
}
