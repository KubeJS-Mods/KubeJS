package dev.latvian.kubejs.block;

import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.player.PlayerEventJS;
import dev.latvian.kubejs.world.BlockContainerJS;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.world.BlockEvent;

/**
 * @author LatvianModder
 */
public class BlockPlaceEventJS extends PlayerEventJS
{
	public final transient BlockEvent.PlaceEvent event;

	public BlockPlaceEventJS(BlockEvent.PlaceEvent e)
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
		return entityOf(event.getPlayer());
	}

	public BlockContainerJS getBlock()
	{
		return new BlockContainerJS(event.getWorld(), event.getPos());
	}

	public boolean isMainHand()
	{
		return event.getHand() == EnumHand.MAIN_HAND;
	}

	public ItemStackJS getItem()
	{
		return ItemStackJS.of(event.getPlayer().getHeldItem(event.getHand()));
	}
}