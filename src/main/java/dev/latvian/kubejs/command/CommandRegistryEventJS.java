package dev.latvian.kubejs.command;

import com.mojang.brigadier.CommandDispatcher;
import dev.latvian.kubejs.server.ServerEventJS;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraftforge.event.RegisterCommandsEvent;

/**
 * @author LatvianModder
 */
public class CommandRegistryEventJS extends ServerEventJS
{
	private final RegisterCommandsEvent event;

	public CommandRegistryEventJS(RegisterCommandsEvent e)
	{
		event = e;
	}

	public boolean isSinglePlayer()
	{
		return event.getEnvironment() == Commands.EnvironmentType.ALL || event.getEnvironment() == Commands.EnvironmentType.INTEGRATED;
	}

	public CommandDispatcher<CommandSource> getDispatcher()
	{
		return event.getDispatcher();
	}
}