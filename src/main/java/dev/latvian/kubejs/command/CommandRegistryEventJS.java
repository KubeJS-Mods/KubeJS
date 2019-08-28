package dev.latvian.kubejs.command;

import dev.latvian.kubejs.event.EventJS;
import net.minecraft.command.CommandBase;

import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class CommandRegistryEventJS extends EventJS
{
	private final Consumer<CommandBase> callback;

	public CommandRegistryEventJS(Consumer<CommandBase> c)
	{
		callback = c;
	}

	public CommandProperties newCommand(String name)
	{
		return new CommandProperties(callback, name);
	}

	public void registerSimpleCommand(String name, CommandProperties.ExecuteFunction execute)
	{
		CommandProperties properties = new CommandProperties(callback, name);
		properties.execute = execute;
		properties.register();
	}
}