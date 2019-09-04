package dev.latvian.kubejs.block;

import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.player.PlayerEventJS;
import dev.latvian.kubejs.util.Facing;
import dev.latvian.kubejs.world.BlockContainerJS;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

/**
 * @author LatvianModder
 */
public class BlockRightClickEventJS extends PlayerEventJS
{
	public final BlockPos pos;
	public final int x, y, z;
	public final boolean mainHand;
	public final Facing facing;

	public BlockRightClickEventJS(PlayerInteractEvent.RightClickBlock event)
	{
		super(event.getEntityPlayer());
		pos = event.getPos();
		x = pos.getX();
		y = pos.getY();
		z = pos.getZ();
		mainHand = event.getHand() == EnumHand.MAIN_HAND;
		facing = event.getFace() == null ? null : Facing.VALUES[event.getFace().getIndex()];
	}

	public ItemStackJS item()
	{
		return player.inventory().getHandItem(mainHand);
	}

	public BlockContainerJS block()
	{
		return player.world.block(pos);
	}
}