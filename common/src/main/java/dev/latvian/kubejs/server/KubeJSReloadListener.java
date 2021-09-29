package dev.latvian.kubejs.server;

import dev.latvian.kubejs.core.RecipeManagerKJS;
import dev.latvian.kubejs.recipe.RecipesAfterLoadEventJS;
import net.minecraft.server.ServerResources;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

public class KubeJSReloadListener implements ResourceManagerReloadListener {
	public static ServerResources resources;

	@Override
	public void onResourceManagerReload(ResourceManager resourceManager) {
		if (resources != null && resources.getRecipeManager() instanceof RecipeManagerKJS) {
			RecipesAfterLoadEventJS.post((RecipeManagerKJS) resources.getRecipeManager());
		}
	}
}
