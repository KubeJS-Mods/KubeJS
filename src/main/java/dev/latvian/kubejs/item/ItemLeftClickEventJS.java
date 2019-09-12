package dev.latvian.kubejs.item;

import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.player.PlayerEventJS;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

/**
 * @author LatvianModder
 */
public class ItemLeftClickEventJS extends PlayerEventJS
{
	public final PlayerInteractEvent.LeftClickEmpty event;

	public ItemLeftClickEventJS(PlayerInteractEvent.LeftClickEmpty e)
	{
		event = e;
	}

	@Override
	public EntityJS getEntity()
	{
		return entityOf(event);
	}

	public ItemStackJS getItem()
	{
		return ItemStackJS.of(event.getItemStack());
	}
}