package dev.latvian.kubejs.item;

import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.player.PlayerEventJS;
import net.minecraft.util.Hand;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

/**
 * @author LatvianModder
 */
public class ItemRightClickEventJS extends PlayerEventJS
{
	public final PlayerInteractEvent.RightClickItem event;

	public ItemRightClickEventJS(PlayerInteractEvent.RightClickItem e)
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

	public Hand getHand()
	{
		return event.getHand();
	}

	public ItemStackJS getItem()
	{
		return ItemStackJS.of(event.getItemStack());
	}
}