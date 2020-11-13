package dev.latvian.kubejs.block.forge;

import dev.latvian.kubejs.entity.EntityEventJS;
import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.world.BlockContainerJS;
import net.minecraft.block.BlockState;
import net.minecraftforge.event.world.BlockEvent;

/**
 * @author LatvianModder
 */
public class BlockPlaceEventJS extends EntityEventJS
{
	public final BlockEvent.EntityPlaceEvent event;

	public BlockPlaceEventJS(BlockEvent.EntityPlaceEvent e)
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
		return entityOf(event.getEntity());
	}

	public BlockContainerJS getBlock()
	{
		return new BlockContainerJS(event.getWorld(), event.getPos())
		{
			@Override
			public BlockState getBlockState()
			{
				return event.getState();
			}
		};
	}
}