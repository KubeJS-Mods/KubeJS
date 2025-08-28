package dev.latvian.mods.kubejs.server;

import dev.latvian.mods.kubejs.plugin.builtin.event.ServerEvents;
import dev.latvian.mods.kubejs.recipe.AfterRecipesLoadedKubeEvent;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

public record KubeJSReloadListener(ReloadableServerResources resources) implements ResourceManagerReloadListener {
	@Override
	public void onResourceManagerReload(ResourceManager resourceManager) {
		if (ServerEvents.RECIPES_AFTER_LOADED.hasListeners()) {
			ServerEvents.RECIPES_AFTER_LOADED.post(ScriptType.SERVER, new AfterRecipesLoadedKubeEvent(resources));
		}
	}
}
