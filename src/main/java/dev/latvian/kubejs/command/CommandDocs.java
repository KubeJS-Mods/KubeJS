package dev.latvian.kubejs.command;

import dev.latvian.kubejs.documentation.Documentation;
import dev.latvian.kubejs.server.ServerJS;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

/**
 * @author LatvianModder
 */
public class CommandDocs extends CommandBase
{
	@Override
	public String getName()
	{
		return "docs";
	}

	@Override
	public String getUsage(ICommandSender sender)
	{
		return "commands.kubejs.docs.usage";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args)
	{
		if (args.length >= 1)
		{
			try
			{
				Documentation.INSTANCE.sendDocs(new CommandSender(ServerJS.instance, sender), Class.forName(args[0]));
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
		else
		{
			Documentation.INSTANCE.sendDocs(new CommandSender(ServerJS.instance, sender));
		}
	}
}