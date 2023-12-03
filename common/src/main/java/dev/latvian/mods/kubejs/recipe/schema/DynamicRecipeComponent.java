package dev.latvian.mods.kubejs.recipe.schema;

import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.typings.desc.TypeDescJS;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public record DynamicRecipeComponent(TypeDescJS desc, Factory factory) {
	public interface Factory {
		@Nullable
		RecipeComponent<?> create(Context cx, Scriptable scope, Map<String, Object> args);
	}
}
