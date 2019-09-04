package dev.latvian.kubejs.item;

import dev.latvian.kubejs.player.PlayerEventJS;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

/**
 * @author LatvianModder
 */
public class ItemRightClickEmptyEventJS extends PlayerEventJS
{
	public final boolean mainHand;

	public ItemRightClickEmptyEventJS(PlayerInteractEvent.RightClickEmpty event)
	{
		super(event.getEntityPlayer());
		mainHand = event.getHand() == EnumHand.MAIN_HAND;
	}
}