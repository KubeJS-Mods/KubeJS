package com.latmod.mods.worldjs.command;

import com.latmod.mods.worldjs.WorldJSMod;
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
		return "commands.worldjs.reload.usage";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args)
	{
		WorldJSMod.loadScripts();

		if (!WorldJSMod.ERRORS.isEmpty())
		{
			for (EntityPlayerMP player : server.getPlayerList().getPlayers())
			{
				if (player.canUseCommand(1, "worldjs.errors"))
				{
					for (ITextComponent component : WorldJSMod.ERRORS)
					{
						player.sendMessage(component);
					}
				}
			}
		}
	}
}