package dev.latvian.mods.kubejs.recipe.component;

import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.rhino.Context;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;

public class ComponentValueMap extends Reference2ObjectOpenHashMap<RecipeKey<?>, Object> {
	public ComponentValueMap(int init) {
		super(init);
	}

	public <T> T getValue(Context cx, KubeRecipe recipe, RecipeKey<T> key) {
		var o = get(key);

		if (o == null) {
			if (key.optional()) {
				return null;
			}

			throw new KubeRuntimeException("Value for '" + key + "' is missing!");
		}

		try {
			return key.component.wrap(cx, recipe, o);
		} catch (Throwable ex) {
			throw new KubeRuntimeException("Unable to cast '" + key + "' value '" + o + "' to '" + key.component + "'!", ex);
		}
	}
}
