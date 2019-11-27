package dev.latvian.kubejs.server;

import dev.latvian.kubejs.event.EventJS;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class ServerEventJS extends EventJS
{
	@Nullable
	public ServerJS getServer()
	{
		return ServerJS.instance;
	}
}