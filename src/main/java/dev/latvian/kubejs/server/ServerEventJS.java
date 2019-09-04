package dev.latvian.kubejs.server;

import dev.latvian.kubejs.event.EventJS;

/**
 * @author LatvianModder
 */
public abstract class ServerEventJS extends EventJS
{
	public abstract ServerJS getServer();
}