package dev.latvian.kubejs.command;

import com.mojang.brigadier.CommandDispatcher;
import dev.latvian.kubejs.server.ServerEventJS;
import net.minecraft.command.CommandSource;

/**
 * @author LatvianModder
 */
public class CommandRegistryEventJS extends ServerEventJS
{
	private final boolean singlePlayer;
	private final CommandDispatcher<CommandSource> dispatcher;

	public CommandRegistryEventJS(boolean s, CommandDispatcher<CommandSource> c)
	{
		singlePlayer = s;
		dispatcher = c;
	}

	public boolean isSinglePlayer()
	{
		return singlePlayer;
	}

	public CommandDispatcher<CommandSource> getDispatcher()
	{
		return dispatcher;
	}
}