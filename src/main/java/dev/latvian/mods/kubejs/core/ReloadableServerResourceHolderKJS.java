package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.server.ServerScriptManager;

public interface ReloadableServerResourceHolderKJS {
	default void kjs$setResources(ReloadableServerResourcesKJS resources) {
		throw new NoMixinException();
	}

	default ServerScriptManager kjs$getServerScriptManager() {
		throw new NoMixinException();
	}
}