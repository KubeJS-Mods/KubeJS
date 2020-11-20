package dev.latvian.kubejs.server;

import dev.latvian.kubejs.event.EventJS;
import org.jetbrains.annotations.Nullable;

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