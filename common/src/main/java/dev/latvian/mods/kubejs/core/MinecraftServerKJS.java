package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.server.ServerJS;
import net.minecraft.server.MinecraftServer;

public interface MinecraftServerKJS extends AsKJS {
	@Override
	default Object asKJS() {
		return ServerJS.instance;
	}

	MinecraftServer.ReloadableResources getReloadableResourcesKJS();
}
