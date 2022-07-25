package dev.latvian.mods.kubejs.block;

import dev.latvian.mods.kubejs.entity.EntityEventJS;
import dev.latvian.mods.kubejs.level.BlockContainerJS;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * @author LatvianModder
 */
public class BlockPlacedEventJS extends EntityEventJS {
	private final Entity entity;
	private final Level level;
	private final BlockPos pos;
	private final BlockState state;

	public BlockPlacedEventJS(@Nullable Entity entity, Level level, BlockPos pos, BlockState state) {
		this.entity = entity;
		this.level = level;
		this.pos = pos;
		this.state = state;
	}

	@Override
	public Level getLevel() {
		return level;
	}

	@Override
	public Entity getEntity() {
		return entity;
	}

	public BlockContainerJS getBlock() {
		return new BlockContainerJS(level, pos) {
			@Override
			public BlockState getBlockState() {
				return state;
			}
		};
	}
}