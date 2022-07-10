package dev.latvian.mods.kubejs.server;

import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.EventJS;
import org.jetbrains.annotations.Nullable;

/**
 * @author LatvianModder
 */
public class ServerEventJS extends EventJS {
	public static final EventHandler LOAD_EVENT = EventHandler.server(ServerEventJS.class).legacy("server.load");
	public static final EventHandler UNLOAD_EVENT = EventHandler.server(ServerEventJS.class).legacy("server.unload");
	public static final EventHandler TICK_EVENT = EventHandler.server(ServerEventJS.class).legacy("server.tick");

	@Nullable
	public ServerJS getServer() {
		return ServerJS.instance;
	}
}