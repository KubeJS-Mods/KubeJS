package dev.latvian.kubejs.item;

import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.player.PlayerEventJS;
import net.minecraftforge.event.entity.item.ItemTossEvent;

/**
 * @author LatvianModder
 */
public class ItemTossEventJS extends PlayerEventJS
{
	public final ItemTossEvent event;

	public ItemTossEventJS(ItemTossEvent e)
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
		return entityOf(event.getPlayer());
	}

	public EntityJS getItemEntity()
	{
		return getWorld().getEntity(event.getEntityItem());
	}

	public ItemStackJS getItem()
	{
		return ItemStackJS.of(event.getEntityItem().getItem());
	}
}