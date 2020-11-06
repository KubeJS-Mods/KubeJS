package dev.latvian.kubejs.command;

import com.mojang.brigadier.CommandDispatcher;
import dev.latvian.kubejs.server.ServerEventJS;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
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
		return event.getEnvironment() == Commands.CommandSelection.ALL || event.getEnvironment() == Commands.CommandSelection.INTEGRATED;
	}

	public CommandDispatcher<CommandSourceStack> getDispatcher()
	{
		return event.getDispatcher();
	}
}