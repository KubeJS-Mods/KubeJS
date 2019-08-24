package dev.latvian.kubejs.server;

import dev.latvian.kubejs.event.EventJS;

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