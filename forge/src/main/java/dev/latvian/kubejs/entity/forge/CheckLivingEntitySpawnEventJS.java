package dev.latvian.kubejs.entity.forge;

import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.entity.LivingEntityEventJS;
import dev.latvian.kubejs.world.BlockContainerJS;
import dev.latvian.kubejs.world.WorldJS;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;

/**
 * @author LatvianModder
 */
public class CheckLivingEntitySpawnEventJS extends LivingEntityEventJS
{
	public final LivingSpawnEvent.CheckSpawn event;

	public CheckLivingEntitySpawnEventJS(LivingSpawnEvent.CheckSpawn e)
	{
		event = e;
	}

	@Override
	public boolean canCancel()
	{
		return true;
	}

	@Override
	public WorldJS getWorld()
	{
		return worldOf((World) event.getWorld());
	}

	@Override
	public EntityJS getEntity()
	{
		return entityOf(event.getEntity());
	}

	public double getX()
	{
		return event.getX();
	}

	public double getY()
	{
		return event.getY();
	}

	public double getZ()
	{
		return event.getZ();
	}

	public BlockContainerJS getBlock()
	{
		return new BlockContainerJS(event.getWorld(), new BlockPos(getX(), getY(), getZ()));
	}
}