package com.latmod.mods.kubejs.world;

import com.latmod.mods.kubejs.entity.EntityJS;
import com.latmod.mods.kubejs.util.ServerJS;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class WorldJS
{
	private final WorldServer world;
	public final ServerJS server;
	public final int dimension;

	public WorldJS(ServerJS s, WorldServer w)
	{
		server = s;
		world = w;
		dimension = world.provider.getDimension();
	}

	public long getLocalTime()
	{
		return world.getWorldTime();
	}

	public long getTotalTime()
	{
		return world.getTotalWorldTime();
	}

	public boolean isDaytime()
	{
		return world.isDaytime();
	}

	public boolean isRaining()
	{
		return world.isRaining();
	}

	public BlockContainerJS block(int x, int y, int z)
	{
		return new BlockContainerJS(world, new BlockPos(x, y, z));
	}

	@Nullable
	public EntityJS entity(@Nullable Entity entity)
	{
		if (entity instanceof EntityPlayerMP)
		{
			return server.playerMap.get(entity.getUniqueID());
		}

		return entity == null ? null : new EntityJS(this, entity);
	}
}