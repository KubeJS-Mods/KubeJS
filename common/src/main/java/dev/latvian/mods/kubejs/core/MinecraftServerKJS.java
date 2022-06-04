package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.server.ServerJS;
import net.minecraft.server.MinecraftServer;

public interface MinecraftServerKJS extends AsKJS<ServerJS> {
	@Override
	default ServerJS asKJS() {
		return ServerJS.instance;
	}

	MinecraftServer.ReloadableResources getReloadableResourcesKJS();
}
