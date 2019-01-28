package com.latmod.mods.kubejs.command;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.server.command.CommandTreeBase;

import java.util.Collections;
import java.util.List;

public class CommandKubeJS extends CommandTreeBase
{
	public CommandKubeJS()
	{
		addSubcommand(new CommandReloadScripts());
	}

	@Override
	public String getName()
	{
		return "kubejs";
	}

	@Override
	public List<String> getAliases()
	{
		return Collections.singletonList("wjs");
	}

	@Override
	public String getUsage(ICommandSender sender)
	{
		return "commands.kubejs.usage";
	}
}