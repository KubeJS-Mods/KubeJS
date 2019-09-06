package dev.latvian.kubejs.entity;

import net.minecraftforge.event.entity.EntityJoinWorldEvent;

/**
 * @author LatvianModder
 */
public class EntitySpawnedEventJS extends EntityEventJS
{
	public final transient EntityJoinWorldEvent event;

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
	public EntityJS getEntity()
	{
		return entityOf(event);
	}
}