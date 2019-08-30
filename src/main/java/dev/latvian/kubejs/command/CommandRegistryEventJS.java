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
	private final Consumer<CommandBase> callback;

	public CommandRegistryEventJS(ServerJS s, Consumer<CommandBase> c)
	{
		super(s);
		callback = c;
	}

	public CommandProperties newCommand(String name)
	{
		return new CommandProperties(callback, name);
	}
}