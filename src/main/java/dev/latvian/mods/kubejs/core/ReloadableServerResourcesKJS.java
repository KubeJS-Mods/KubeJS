package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.server.ServerScriptManager;

public interface ReloadableServerResourcesKJS {
	default ServerScriptManager kjs$getServerScriptManager() {
		throw new NoMixinException();
	}
}
