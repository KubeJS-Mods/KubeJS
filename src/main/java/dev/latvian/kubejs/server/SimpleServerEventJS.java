package dev.latvian.kubejs.server;

/**
 * @author LatvianModder
 */
public class SimpleServerEventJS extends ServerEventJS
{
	private final ServerJS server;

	public SimpleServerEventJS(ServerJS s)
	{
		server = s;
	}

	@Override
	public ServerJS getServer()
	{
		return server;
	}
}