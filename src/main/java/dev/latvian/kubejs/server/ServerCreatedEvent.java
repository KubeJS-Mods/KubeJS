package dev.latvian.kubejs.server;

import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * @author LatvianModder
 */
public class ServerCreatedEvent extends Event
{
	private final ServerJS server;

	public ServerCreatedEvent(ServerJS s)
	{
		server = s;
	}

	public ServerJS getServer()
	{
		return server;
	}

	public void setData(String id, Object object)
	{
		server.data.put(id, object);
	}
}