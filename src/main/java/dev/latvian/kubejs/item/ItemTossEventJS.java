package dev.latvian.kubejs.item;

import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.player.PlayerEventJS;
import net.minecraft.entity.item.EntityItem;
import net.minecraftforge.event.entity.item.ItemTossEvent;

/**
 * @author LatvianModder
 */
public class ItemTossEventJS extends PlayerEventJS
{
	public final EntityItem itemEntity;

	public ItemTossEventJS(ItemTossEvent event)
	{
		super(event.getPlayer());
		itemEntity = event.getEntityItem();
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