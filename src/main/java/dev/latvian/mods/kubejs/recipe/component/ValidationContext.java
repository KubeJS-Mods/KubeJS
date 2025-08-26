package dev.latvian.mods.kubejs.recipe.component;

import dev.latvian.mods.kubejs.recipe.RecipesKubeEvent;
import dev.latvian.mods.kubejs.util.ErrorStack;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import dev.latvian.mods.kubejs.util.RegistryOpsContainer;

public record ValidationContext(RegistryAccessContainer registries, RegistryOpsContainer ops, ErrorStack stack) {
	public ValidationContext(RecipesKubeEvent event, ErrorStack stack) {
		this(event.registries, event.ops, stack);
	}
}
