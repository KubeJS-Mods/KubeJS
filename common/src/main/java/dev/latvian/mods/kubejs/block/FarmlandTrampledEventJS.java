package dev.latvian.mods.kubejs.block;

import dev.latvian.mods.kubejs.entity.EntityEventJS;
import dev.latvian.mods.kubejs.level.BlockContainerJS;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class FarmlandTrampledEventJS extends EntityEventJS {
	private final Level level;
	private final BlockContainerJS block;
	private final float distance;
	private final Entity entity;

	public FarmlandTrampledEventJS(Level l, BlockPos pos, BlockState state, float d, Entity e) {
		level = l;
		block = level.kjs$getBlock(pos);
		block.cachedState = state;
		distance = d;
		entity = e;
	}

	public float getDistance() {
		return distance;
	}

	@Override
	public Entity getEntity() {
		return entity;
	}

	@Override
	public Level getLevel() {
		return level;
	}

	public BlockContainerJS getBlock() {
		return block;
	}
}
