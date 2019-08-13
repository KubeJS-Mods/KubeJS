package dev.latvian.kubejs.world;

import dev.latvian.kubejs.events.EventJS;
import dev.latvian.kubejs.util.ServerJS;

/**
 * @author LatvianModder
 */
public class ServerEventJS extends EventJS
{
	public final ServerJS server;

	public ServerEventJS(ServerJS s)
	{
		server = s;
	}
}