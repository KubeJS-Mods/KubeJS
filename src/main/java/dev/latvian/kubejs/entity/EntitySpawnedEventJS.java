package dev.latvian.kubejs.entity;

import dev.latvian.kubejs.world.WorldJS;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

/**
 * @author LatvianModder
 */
public class EntitySpawnedEventJS extends EntityEventJS
{
	public final EntityJoinWorldEvent event;

	public EntitySpawnedEventJS(EntityJoinWorldEvent e)
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
		return worldOf(event.getWorld());
	}

	@Override
	public EntityJS getEntity()
	{
		return entityOf(event);
	}
}