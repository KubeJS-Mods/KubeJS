package dev.latvian.mods.kubejs.recipe;

import dev.latvian.mods.kubejs.core.RecipeLikeKJS;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import dev.latvian.mods.kubejs.util.RegistryOpsContainer;

public interface RecipeLikeContext {
	RegistryAccessContainer registries();

	RegistryOpsContainer ops();

	RecipeLikeKJS recipe();
}
