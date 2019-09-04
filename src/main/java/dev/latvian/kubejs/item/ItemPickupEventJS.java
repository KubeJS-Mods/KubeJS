package dev.latvian.kubejs.item;

import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.player.PlayerEventJS;
import net.minecraft.entity.item.EntityItem;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;

/**
 * @author LatvianModder
 */
public class ItemPickupEventJS extends PlayerEventJS
{
	public final EntityItem itemEntity;

	public ItemPickupEventJS(EntityItemPickupEvent event)
	{
		super(event.getEntityPlayer());
		itemEntity = event.getItem();
	}

	public ItemStackJS item()
	{
		return ItemStackJS.of(itemEntity.getItem());
	}

	public EntityJS entity()
	{
		return world.entity(itemEntity);
	}

	@Override
	public boolean canCancel()
	{
		return true;
	}
}