package com.latmod.mods.kubejs.command;

import com.latmod.mods.kubejs.KubeJS;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;

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
		KubeJS.loadScripts();

		if (!KubeJS.ERRORS.isEmpty())
		{
			for (EntityPlayerMP player : server.getPlayerList().getPlayers())
			{
				if (player.canUseCommand(1, "kubejs.errors"))
				{
					for (ITextComponent component : KubeJS.ERRORS)
					{
						player.sendMessage(component);
					}
				}
			}
		}
	}
}