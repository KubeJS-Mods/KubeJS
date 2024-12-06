package dev.latvian.mods.kubejs.block.callback;

import dev.latvian.mods.kubejs.level.LevelBlock;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class EntitySteppedOnBlockCallback {
	protected final Level level;
	protected final Entity entity;
	protected final LevelBlock block;
	protected final BlockState state;

	public EntitySteppedOnBlockCallback(Level level, Entity entity, BlockPos pos, BlockState state) {
		this.level = level;
		this.entity = entity;
		this.block = level.kjs$getBlock(pos);
		this.state = state;
	}

	@Info("Returns the level")
	public Level getLevel() {
		return level;
	}

	@Info("Returns the entity")
	public Entity getEntity() {
		return entity;
	}

	@Info("Returns the block")
	public LevelBlock getBlock() {
		return block;
	}

	@Info("Returns the BlockState")
	public BlockState getState() {
		return state;
	}

	@Info("Returns the block's position")
	public BlockPos getPos() {
		return block.getPos();
	}

	@Info("Returns if the entity is suppressing bouncing (for players this is true if the player is crouching)")
	public boolean isSuppressingBounce() {
		return entity.isSuppressingBounce();
	}
}
