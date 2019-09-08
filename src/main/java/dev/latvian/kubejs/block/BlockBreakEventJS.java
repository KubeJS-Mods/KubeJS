package dev.latvian.kubejs.block;

import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.player.PlayerEventJS;
import dev.latvian.kubejs.world.BlockContainerJS;
import net.minecraftforge.event.world.BlockEvent;

/**
 * @author LatvianModder
 */
public class BlockBreakEventJS extends PlayerEventJS
{
	public final transient BlockEvent.BreakEvent event;

	public BlockBreakEventJS(BlockEvent.BreakEvent e)
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

	public int getXp()
	{
		return event.getExpToDrop();
	}

	public void setXp(int xp)
	{
		event.setExpToDrop(xp);
	}
}