package dev.latvian.kubejs.entity;

import dev.latvian.kubejs.world.WorldEventJS;
import dev.latvian.kubejs.world.WorldJS;
import net.minecraft.entity.Entity;
import net.minecraftforge.event.entity.EntityEvent;

/**
 * @author LatvianModder
 */
public abstract class EntityEventJS extends WorldEventJS
{
	private EntityJS cachedEntity;

	public abstract EntityJS getEntity();

	@Override
	public final WorldJS getWorld()
	{
		return getEntity().world;
	}

	protected EntityJS entityOf(Entity entity)
	{
		if (cachedEntity == null)
		{
			cachedEntity = worldOf(entity).getEntity(entity);

			if (cachedEntity == null)
			{
				throw new IllegalStateException("Entity can't be null!");
			}
		}

		return cachedEntity;
	}

	protected EntityJS entityOf(EntityEvent event)
	{
		return entityOf(event.getEntity());
	}
}