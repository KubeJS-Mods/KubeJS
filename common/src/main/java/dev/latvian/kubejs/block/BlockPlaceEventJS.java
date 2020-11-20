package dev.latvian.kubejs.block;

import dev.latvian.kubejs.entity.EntityEventJS;
import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.world.BlockContainerJS;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * @author LatvianModder
 */
public class BlockPlaceEventJS extends EntityEventJS
{
	public final Entity entity;
	public final Level world;
	public final BlockPos pos;
	public final BlockState state;

	public BlockPlaceEventJS(Entity entity, Level world, BlockPos pos, BlockState state)
	{
		this.entity = entity;
		this.world = world;
		this.pos = pos;
		this.state = state;
	}

	@Override
	public boolean canCancel()
	{
		return true;
	}

	@Override
	public EntityJS getEntity()
	{
		return entityOf(entity);
	}

	public BlockContainerJS getBlock()
	{
		return new BlockContainerJS(world, pos)
		{
			@Override
			public BlockState getBlockState()
			{
				return state;
			}
		};
	}
}