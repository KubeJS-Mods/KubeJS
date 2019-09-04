package dev.latvian.kubejs.item;

import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.player.PlayerEventJS;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

/**
 * @author LatvianModder
 */
public class ItemLeftClickEventJS extends PlayerEventJS
{
	public final transient PlayerInteractEvent.LeftClickEmpty event;

	public ItemLeftClickEventJS(PlayerInteractEvent.LeftClickEmpty e)
	{
		event = e;
	}

	@Override
	public EntityJS getEntity()
	{
		return entityOf(event);
	}
}