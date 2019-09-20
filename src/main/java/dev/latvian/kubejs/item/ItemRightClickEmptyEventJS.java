package dev.latvian.kubejs.item;

import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.player.PlayerEventJS;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

/**
 * @author LatvianModder
 */
public class ItemRightClickEmptyEventJS extends PlayerEventJS
{
	public final PlayerInteractEvent.RightClickEmpty event;

	public ItemRightClickEmptyEventJS(PlayerInteractEvent.RightClickEmpty e)
	{
		event = e;
	}

	@Override
	public EntityJS getEntity()
	{
		return entityOf(event);
	}

	public EnumHand getHand()
	{
		return event.getHand();
	}

	public ItemStackJS getItem()
	{
		return EmptyItemStackJS.INSTANCE;
	}
}