package dev.latvian.kubejs.item;

import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.player.PlayerEventJS;
import net.minecraftforge.event.entity.player.PlayerEvent;

/**
 * @author LatvianModder
 */
public class ItemCraftedEventJS extends PlayerEventJS
{
	public final PlayerEvent.ItemCraftedEvent event;

	public ItemCraftedEventJS(PlayerEvent.ItemCraftedEvent e)
	{
		event = e;
	}

	@Override
	public EntityJS getEntity()
	{
		return entityOf(event.getPlayer());
	}

	public ItemStackJS getItem()
	{
		return ItemStackJS.of(event.getCrafting());
	}

	public InventoryJS getInventory()
	{
		return new InventoryJS(event.getInventory());
	}
}