package dev.latvian.mods.kubejs.block.callbacks;

import dev.latvian.mods.kubejs.level.BlockContainerJS;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class EntityStepOnBlockCallbackJS {

	protected final Level level;
	protected final Entity entity;
	protected final BlockContainerJS block;
	protected final BlockState state;

	public EntityStepOnBlockCallbackJS(Level level, Entity entity, BlockPos pos, BlockState state) {
		this.level = level;
		this.entity = entity;
		this.block = new BlockContainerJS(level, pos);
		this.state = state;
	}

	public Level getLevel() {
		return level;
	}

	public Entity getEntity() {
		return entity;
	}

	public BlockContainerJS getBlock() {
		return block;
	}

	public BlockState getState() {
		return state;
	}

	public BlockPos getPos() {
		return block.getPos();
	}
}
