package dev.latvian.kubejs.item;

import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.player.PlayerEventJS;
import net.minecraftforge.event.entity.player.PlayerEvent;

/**
 * @author LatvianModder
 */
public class ItemSmeltedEventJS extends PlayerEventJS
{
	public final PlayerEvent.ItemSmeltedEvent event;

	public ItemSmeltedEventJS(PlayerEvent.ItemSmeltedEvent e)
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
		return ItemStackJS.of(event.getSmelting());
	}
}