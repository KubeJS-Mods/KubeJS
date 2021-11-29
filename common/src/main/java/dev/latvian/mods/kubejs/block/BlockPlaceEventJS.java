package dev.latvian.mods.kubejs.block;

import dev.latvian.mods.kubejs.entity.EntityEventJS;
import dev.latvian.mods.kubejs.entity.EntityJS;
import dev.latvian.mods.kubejs.world.BlockContainerJS;
import dev.latvian.mods.kubejs.world.WorldJS;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * @author LatvianModder
 */
public class BlockPlaceEventJS extends EntityEventJS {
	private final Entity entity;
	private final Level world;
	private final BlockPos pos;
	private final BlockState state;

	public BlockPlaceEventJS(@Nullable Entity entity, Level world, BlockPos pos, BlockState state) {
		this.entity = entity;
		this.world = world;
		this.pos = pos;
		this.state = state;
	}

	@Override
	public boolean canCancel() {
		return true;
	}

	@Override
	public WorldJS getWorld() {
		return worldOf(world);
	}

	@Override
	public EntityJS getEntity() {
		return entity == null ? null : entityOf(entity);
	}

	public BlockContainerJS getBlock() {
		return new BlockContainerJS(world, pos) {
			@Override
			public BlockState getBlockState() {
				return state;
			}
		};
	}
}