package dev.latvian.kubejs.world;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class WorldCommandSender implements ICommandSender
{
	private final WorldJS world;

	public WorldCommandSender(WorldJS w)
	{
		world = w;
	}

	public WorldJS getWorld()
	{
		return world;
	}

	@Override
	public String getName()
	{
		return "DIM" + world.minecraftWorld.provider.getDimension();
	}

	@Override
	public boolean canUseCommand(int permLevel, String commandName)
	{
		return true;
	}

	@Override
	public World getEntityWorld()
	{
		return world.minecraftWorld;
	}

	@Nullable
	@Override
	public MinecraftServer getServer()
	{
		if (world instanceof ServerWorldJS)
		{
			return world.getServer().minecraftServer;
		}

		return null;
	}
}