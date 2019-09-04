package dev.latvian.kubejs.block;

import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.player.PlayerEventJS;
import dev.latvian.kubejs.world.BlockContainerJS;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.world.BlockEvent;

/**
 * @author LatvianModder
 */
public class BlockPlaceEventJS extends PlayerEventJS
{
	public final BlockPos pos;
	public final int x, y, z;
	public final boolean mainHand;

	public BlockPlaceEventJS(BlockEvent.PlaceEvent event)
	{
		super(event.getPlayer());
		pos = event.getPos();
		x = pos.getX();
		y = pos.getY();
		z = pos.getZ();
		mainHand = event.getHand() == EnumHand.MAIN_HAND;
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