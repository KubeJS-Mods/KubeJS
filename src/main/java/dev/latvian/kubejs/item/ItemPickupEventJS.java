package dev.latvian.kubejs.item;

import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.player.PlayerEventJS;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;

/**
 * @author LatvianModder
 */
public class ItemPickupEventJS extends PlayerEventJS
{
	public final EntityItemPickupEvent event;

	public ItemPickupEventJS(EntityItemPickupEvent e)
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

	public EntityJS getItemEntity()
	{
		return getWorld().getEntity(event.getItem());
	}

	public ItemStackJS getItem()
	{
		return ItemStackJS.of(event.getItem().getItem());
	}
}