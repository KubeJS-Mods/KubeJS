package dev.latvian.mods.kubejs.recipe;

import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import dev.latvian.mods.kubejs.util.RegistryOpsContainer;

public interface KubeRecipeContext extends RecipeLikeContext {
	@Override
	default RegistryAccessContainer registries() {
		return recipe().type.event.registries;
	}

	@Override
	default RegistryOpsContainer ops() {
		return recipe().type.event.ops;
	}

	@Override
	KubeRecipe recipe();
}
