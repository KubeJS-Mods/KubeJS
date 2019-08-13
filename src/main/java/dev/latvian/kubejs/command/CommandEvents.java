package dev.latvian.kubejs.command;

import dev.latvian.kubejs.events.EventsJS;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

import java.util.Map;

/**
 * @author LatvianModder
 */
public class CommandEvents extends CommandBase
{
	@Override
	public String getName()
	{
		return "events";
	}

	@Override
	public String getUsage(ICommandSender sender)
	{
		return "commands.kubejs.events.usage";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args)
	{
		for (Map.Entry<String, Class> entry : EventsJS.INSTANCE.list().entrySet())
		{
			sender.sendMessage(new TextComponentString(entry + ": " + entry.getValue().getName()));
		}
	}
}