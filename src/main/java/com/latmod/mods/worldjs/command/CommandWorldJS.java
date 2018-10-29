package com.latmod.mods.worldjs.command;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.server.command.CommandTreeBase;

import java.util.Collections;
import java.util.List;

public class CommandWorldJS extends CommandTreeBase
{
	public CommandWorldJS()
	{
		addSubcommand(new CommandReloadScripts());
	}

	@Override
	public String getName()
	{
		return "worldjs";
	}

	@Override
	public List<String> getAliases()
	{
		return Collections.singletonList("wjs");
	}

	@Override
	public String getUsage(ICommandSender sender)
	{
		return "commands.worldjs.usage";
	}
}