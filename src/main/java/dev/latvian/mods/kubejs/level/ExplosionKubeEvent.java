package dev.latvian.mods.kubejs.level;

import dev.latvian.mods.kubejs.player.EntityArrayList;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class ExplosionKubeEvent implements KubeLevelEvent {
	protected final Level level;
	protected final Explosion explosion;

	public ExplosionKubeEvent(Level level, Explosion explosion) {
		this.level = level;
		this.explosion = explosion;
	}

	@Override
	public Level getLevel() {
		return level;
	}

	public Vec3 getPosition() {
		return explosion.center();
	}

	public double getX() {
		return getPosition().x;
	}

	public double getY() {
		return getPosition().y;
	}

	public double getZ() {
		return getPosition().z;
	}

	public BlockContainerJS getBlock() {
		return new BlockContainerJS(level, BlockPos.containing(getPosition()));
	}

	@Nullable
	public LivingEntity getExploder() {
		return explosion.getIndirectSourceEntity();
	}

	@Info("""
		Invoked right before an explosion happens.
		""")
	public static class Before extends ExplosionKubeEvent {
		public Before(Level level, Explosion explosion) {
			super(level, explosion);
		}

		@Info("Returns the size of the explosion.")
		public float getSize() {
			return explosion.radius;
		}

		@Info("Sets the size of the explosion.")
		public void setSize(float s) {
			explosion.radius = s;
		}
	}

	@Info("""
		Invoked right after an explosion happens.
		""")
	public static class After extends ExplosionKubeEvent {
		private final List<Entity> affectedEntities;

		public After(Level level, Explosion explosion, List<Entity> affectedEntities) {
			super(level, explosion);
			this.affectedEntities = affectedEntities;
		}

		@Info("Gets a list of all entities affected by the explosion.")
		public EntityArrayList getAffectedEntities() {
			return new EntityArrayList(level, affectedEntities);
		}

		@Info("Remove an entity from the list of affected entities.")
		public void removeAffectedEntity(Entity entity) {
			affectedEntities.remove(entity);
		}

		@Info("Remove all entities from the list of affected entities.")
		public void removeAllAffectedEntities() {
			affectedEntities.clear();
		}

		@Info("Gets a list of all blocks affected by the explosion.")
		public List<BlockContainerJS> getAffectedBlocks() {
			List<BlockContainerJS> list = new ArrayList<>(explosion.getToBlow().size());

			for (var pos : explosion.getToBlow()) {
				list.add(new BlockContainerJS(level, pos));
			}

			return list;
		}

		@Info("Remove a block from the list of affected blocks.")
		public void removeAffectedBlock(BlockContainerJS block) {
			explosion.getToBlow().remove(block.getPos());
		}

		@Info("Remove all blocks from the list of affected blocks.")
		public void removeAllAffectedBlocks() {
			explosion.getToBlow().clear();
		}

		@Info("Remove all knockback from all affected *players*.")
		public void removeKnockback() {
			explosion.getHitPlayers().clear();
		}
	}
}