package dev.latvian.mods.kubejs.block;

import dev.latvian.mods.kubejs.entity.KubeEntityEvent;
import dev.latvian.mods.kubejs.level.BlockContainerJS;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

@Info(value = """
	Invoked when a block is placed.
	""")
public class BlockPlacedKubeEvent implements KubeEntityEvent {
	private final Entity entity;
	private final Level level;
	private final BlockPos pos;
	private final BlockState state;

	public BlockPlacedKubeEvent(@Nullable Entity entity, Level level, BlockPos pos, BlockState state) {
		this.entity = entity;
		this.level = level;
		this.pos = pos;
		this.state = state;
	}

	@Override
	@Info("The level of the block that was placed.")
	public Level getLevel() {
		return level;
	}

	@Override
	@Info("The entity that placed the block. Can be `null`, e.g. when a block is placed by a dispenser.")
	public Entity getEntity() {
		return entity;
	}

	@Info("The block that is placed.")
	public BlockContainerJS getBlock() {
		return new BlockContainerJS(level, pos) {
			@Override
			public BlockState getBlockState() {
				return state;
			}
		};
	}
}