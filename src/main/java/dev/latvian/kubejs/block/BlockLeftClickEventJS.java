package dev.latvian.kubejs.block;

import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.player.PlayerEventJS;
import dev.latvian.kubejs.util.Facing;
import dev.latvian.kubejs.world.BlockContainerJS;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class BlockLeftClickEventJS extends PlayerEventJS
{
	public final transient PlayerInteractEvent.LeftClickBlock event;

	public BlockLeftClickEventJS(PlayerInteractEvent.LeftClickBlock e)
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

	public ItemStackJS getItem()
	{
		return ItemStackJS.of(event.getItemStack());
	}

	@Nullable
	public Facing getFacing()
	{
		return event.getFace() == null ? null : Facing.VALUES[event.getFace().getIndex()];
	}
}