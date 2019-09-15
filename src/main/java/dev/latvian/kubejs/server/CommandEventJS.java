package dev.latvian.kubejs.server;

import dev.latvian.kubejs.command.CommandSender;
import net.minecraftforge.event.CommandEvent;

/**
 * @author LatvianModder
 */
public class CommandEventJS extends ServerEventJS
{
	private final ServerJS server;
	public final CommandEvent event;

	public CommandEventJS(ServerJS s, CommandEvent e)
	{
		server = s;
		event = e;
	}

	@Override
	public boolean canCancel()
	{
		return true;
	}

	@Override
	public ServerJS getServer()
	{
		return server;
	}

	public String getCommand()
	{
		return event.getCommand().getName();
	}

	public CommandSender getSender()
	{
		return new CommandSender(server, event.getSender());
	}

	public String[] getParameters()
	{
		return event.getParameters();
	}

	public void setParameters(String[] parameters)
	{
		event.setParameters(parameters);
	}
}