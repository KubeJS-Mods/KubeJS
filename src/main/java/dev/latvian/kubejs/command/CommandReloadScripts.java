package dev.latvian.kubejs.command;

import dev.latvian.kubejs.ScriptManager;
import dev.latvian.kubejs.util.ScriptFile;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

/**
 * @author LatvianModder
 */
public class CommandReloadScripts extends CommandBase
{
	@Override
	public String getName()
	{
		return "reload";
	}

	@Override
	public String getUsage(ICommandSender sender)
	{
		return "commands.kubejs.reload.usage";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args)
	{
		long time = ScriptManager.instance.load();

		int i = 0;

		for (ScriptFile file : ScriptManager.instance.scripts.values())
		{
			ITextComponent component = file.getErrorTextComponent();

			if (component != null)
			{
				sender.sendMessage(component);
			}
			else
			{
				i++;
			}
		}

		sender.sendMessage(new TextComponentString("Loaded " + i + "/" + ScriptManager.instance.scripts.size() + " scripts in " + (time / 1000D) + "s"));
	}
}