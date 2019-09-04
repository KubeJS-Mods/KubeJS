package dev.latvian.kubejs.item;

import dev.latvian.kubejs.player.PlayerEventJS;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

/**
 * @author LatvianModder
 */
public class ItemRightClickEventJS extends PlayerEventJS
{
	public final boolean mainHand;

	public ItemRightClickEventJS(PlayerInteractEvent.RightClickItem event)
	{
		super(event.getEntityPlayer());
		mainHand = event.getHand() == EnumHand.MAIN_HAND;
	}

	public ItemStackJS item()
	{
		return player.inventory().getHandItem(mainHand);
	}

	@Override
	public boolean canCancel()
	{
		return true;
	}
}