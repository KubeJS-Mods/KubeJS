package dev.latvian.kubejs.entity;

import dev.latvian.kubejs.world.BlockContainerJS;
import dev.latvian.kubejs.world.WorldJS;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
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
		return worldOf((Level) event.getWorld());
	}

	@Override
	public EntityJS getEntity()
	{
		return entityOf(event);
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