package dev.latvian.kubejs.command;

import dev.latvian.kubejs.server.ServerEventJS;
import dev.latvian.kubejs.server.ServerJS;
import net.minecraft.command.CommandBase;

import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class CommandRegistryEventJS extends ServerEventJS
{
	private final ServerJS server;
	private final Consumer<CommandBase> callback;

	public CommandRegistryEventJS(ServerJS s, Consumer<CommandBase> c)
	{
		server = s;
		callback = c;
	}

	@Override
	public ServerJS getServer()
	{
		return server;
	}

	public CommandBuilder create(String name)
	{
		return new CommandBuilder(callback, name);
	}
}