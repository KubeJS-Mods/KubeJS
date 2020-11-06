package dev.latvian.kubejs.block;

import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.player.PlayerEventJS;
import dev.latvian.kubejs.world.BlockContainerJS;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

/**
 * @author LatvianModder
 */
public class BlockRightClickEventJS extends PlayerEventJS
{
	public final PlayerInteractEvent.RightClickBlock event;
	private BlockContainerJS block;
	private ItemStackJS item;

	public BlockRightClickEventJS(PlayerInteractEvent.RightClickBlock e)
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

	public BlockContainerJS getBlock()
	{
		if (block == null)
		{
			block = new BlockContainerJS(event.getWorld(), event.getPos());
		}

		return block;
	}

	public InteractionHand getHand()
	{
		return event.getHand();
	}

	public ItemStackJS getItem()
	{
		if (item == null)
		{
			item = ItemStackJS.of(event.getItemStack());
		}

		return item;
	}

	public Direction getFacing()
	{
		return event.getFace();
	}
}