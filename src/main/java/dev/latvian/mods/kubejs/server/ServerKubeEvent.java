package dev.latvian.mods.kubejs.server;

import dev.latvian.mods.kubejs.event.KubeEvent;
import net.minecraft.server.MinecraftServer;

public class ServerKubeEvent implements KubeEvent {
	public final MinecraftServer server;

	public ServerKubeEvent(MinecraftServer s) {
		server = s;
	}

	public MinecraftServer getServer() {
		return server;
	}
}