package dev.latvian.kubejs.world;

import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.player.LivingEntityJS;
import dev.latvian.kubejs.util.ServerJS;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class WorldJS
{
	public final transient WorldServer world;
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
		if (entity == null)
		{
			return null;
		}
		else if (entity instanceof EntityPlayerMP)
		{
			return server.playerMap.get(entity.getUniqueID());
		}
		else if (entity instanceof EntityLivingBase)
		{
			return new LivingEntityJS(server, (EntityLivingBase) entity);
		}

		return new EntityJS(server, entity);
	}

	public void explosion(double x, double y, double z, float strength, boolean causesFire, boolean damagesTerrain)
	{
		world.newExplosion(null, x, y, z, strength, causesFire, damagesTerrain);
	}
}