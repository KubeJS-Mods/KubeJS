package dev.latvian.mods.kubejs.server;

import dev.latvian.mods.kubejs.bindings.event.ServerEvents;
import dev.latvian.mods.kubejs.recipe.AfterRecipesLoadedEventJS;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

public class KubeJSReloadListener implements ResourceManagerReloadListener {
	public static ReloadableServerResources resources;
	public static Object recipeContext; // Forge

	@Override
	public void onResourceManagerReload(ResourceManager resourceManager) {
		var recipeManager = resources == null ? null : resources.getRecipeManager();

		if (recipeManager != null && ServerEvents.RECIPES_AFTER_LOADED.hasListeners()) {
			ServerEvents.RECIPES_AFTER_LOADED.post(ScriptType.SERVER, new AfterRecipesLoadedEventJS(recipeManager.recipes, recipeManager.byName));
		}
	}
}
