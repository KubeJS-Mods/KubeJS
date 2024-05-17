package dev.latvian.mods.kubejs.block;

import dev.latvian.mods.kubejs.entity.KubeEntityEvent;
import dev.latvian.mods.kubejs.level.BlockContainerJS;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

@Info(value = """
	Invoked when an entity attempts to trample farmland.
	""")
public class FarmlandTrampledKubeEvent implements KubeEntityEvent {
	private final Level level;
	private final BlockContainerJS block;
	private final float distance;
	private final Entity entity;

	public FarmlandTrampledKubeEvent(Level l, BlockPos pos, BlockState state, float d, Entity e) {
		level = l;
		block = level.kjs$getBlock(pos);
		block.cachedState = state;
		distance = d;
		entity = e;
	}

	@Info("The distance of the entity from the block.")
	public float getDistance() {
		return distance;
	}

	@Override
	@Info("The entity that is attempting to trample the farmland.")
	public Entity getEntity() {
		return entity;
	}

	@Override
	@Info("The level that the farmland and the entity are in.")
	public Level getLevel() {
		return level;
	}

	@Info("The farmland block.")
	public BlockContainerJS getBlock() {
		return block;
	}
}
