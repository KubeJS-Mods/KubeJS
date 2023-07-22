package dev.latvian.mods.kubejs.block;

import dev.latvian.mods.kubejs.entity.EntityEventJS;
import dev.latvian.mods.kubejs.level.BlockContainerJS;
import dev.latvian.mods.kubejs.typings.JsInfo;
import dev.latvian.mods.kubejs.typings.JsParam;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

@JsInfo(value = """
		Invoked when a block is placed.
		""")
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
	@JsInfo("The level of the block that was placed.")
	public Level getLevel() {
		return level;
	}

	@Override
	@JsInfo("The entity that placed the block. Can be `null`, e.g. when a block is placed by a dispenser.")
	public Entity getEntity() {
		return entity;
	}

	@JsInfo("The block that is placed.")
	public BlockContainerJS getBlock() {
		return new BlockContainerJS(level, pos) {
			@Override
			public BlockState getBlockState() {
				return state;
			}
		};
	}
}