package dev.latvian.mods.kubejs.server;

import dev.latvian.mods.kubejs.recipe.AfterRecipesLoadedEventJS;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

public class KubeJSReloadListener implements ResourceManagerReloadListener {
	public static ReloadableServerResources resources;
	public static Object recipeContext; // Forge

	@Override
	public void onResourceManagerReload(ResourceManager resourceManager) {
		if (resources != null) {
			AfterRecipesLoadedEventJS.post(resources.getRecipeManager());
		}
	}
}
