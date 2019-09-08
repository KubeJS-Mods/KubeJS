package dev.latvian.kubejs.block;

import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.player.PlayerEventJS;
import dev.latvian.kubejs.util.Facing;
import dev.latvian.kubejs.world.BlockContainerJS;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

/**
 * @author LatvianModder
 */
public class BlockRightClickEventJS extends PlayerEventJS
{
	public final transient PlayerInteractEvent.RightClickBlock event;

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
		return new BlockContainerJS(event.getWorld(), event.getPos());
	}

	public boolean isMainHand()
	{
		return event.getHand() == EnumHand.MAIN_HAND;
	}

	public ItemStackJS getItem()
	{
		return ItemStackJS.of(event.getItemStack());
	}

	public Facing getFacing()
	{
		return Facing.VALUES[event.getFace().getIndex()];
	}
}