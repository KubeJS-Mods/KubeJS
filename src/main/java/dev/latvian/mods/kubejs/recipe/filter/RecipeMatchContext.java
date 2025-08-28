package dev.latvian.mods.kubejs.recipe.filter;

import dev.latvian.mods.kubejs.core.RecipeLikeKJS;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.RecipeLikeContext;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import dev.latvian.mods.kubejs.util.RegistryOpsContainer;
import dev.latvian.mods.rhino.Context;

public interface RecipeMatchContext extends RecipeLikeContext {
	record Impl(Context cx, RegistryAccessContainer registries, RegistryOpsContainer ops, RecipeLikeKJS recipe) implements RecipeMatchContext {
		public Impl(Context cx, KubeRecipe recipe) {
			this(cx, recipe.type.event.registries, recipe.type.event.ops, recipe);
		}
	}
}
