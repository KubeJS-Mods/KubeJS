package dev.latvian.mods.kubejs.server;

import dev.latvian.mods.kubejs.event.EventJS;
import net.minecraft.server.MinecraftServer;

/**
 * @author LatvianModder
 */
public class ServerEventJS extends EventJS {
	public final MinecraftServer server;

	public ServerEventJS(MinecraftServer s) {
		server = s;
	}

	public MinecraftServer getServer() {
		return server;
	}
}