package dev.latvian.kubejs.block;

import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.player.PlayerEventJS;
import dev.latvian.kubejs.world.BlockContainerJS;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.world.BlockEvent;

/**
 * @author LatvianModder
 */
public class BlockBreakEventJS extends PlayerEventJS
{
	public final BlockPos pos;
	public final int x, y, z;
	public int xp;

	public BlockBreakEventJS(BlockEvent.BreakEvent event)
	{
		super(event.getPlayer());
		pos = event.getPos();
		x = pos.getX();
		y = pos.getY();
		z = pos.getZ();
		xp = event.getExpToDrop();
	}

	public ItemStackJS item()
	{
		return player.inventory().getHandItem(true);
	}

	public BlockContainerJS block()
	{
		return player.world.block(pos);
	}
}