package dev.latvian.mods.kubejs.level;

import dev.architectury.hooks.level.ExplosionHooks;
import dev.latvian.mods.kubejs.player.EntityArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class ExplosionEventJS extends LevelEventJS {
	protected final Level level;
	protected final Explosion explosion;

	public ExplosionEventJS(Level level, Explosion explosion) {
		this.level = level;
		this.explosion = explosion;
	}

	@Override
	public Level getLevel() {
		return level;
	}

	public Vec3 getPosition() {
		return ExplosionHooks.getPosition(explosion);
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
		return new BlockContainerJS(level, new BlockPos(getPosition()));
	}

	@Nullable
	public LivingEntity getExploder() {
		return explosion.getSourceMob();
	}

	public static class Before extends ExplosionEventJS {
		public Before(Level level, Explosion explosion) {
			super(level, explosion);
		}

		public float getSize() {
			return explosion.radius;
		}

		public void setSize(float s) {
			explosion.radius = s;
		}
	}

	public static class After extends ExplosionEventJS {
		private final List<Entity> affectedEntities;

		public After(Level level, Explosion explosion, List<Entity> affectedEntities) {
			super(level, explosion);
			this.affectedEntities = affectedEntities;
		}

		public EntityArrayList getAffectedEntities() {
			return new EntityArrayList(level, affectedEntities);
		}

		public void removeAffectedEntity(Entity entity) {
			affectedEntities.remove(entity);
		}

		public void removeAllAffectedEntities() {
			affectedEntities.clear();
		}

		public List<BlockContainerJS> getAffectedBlocks() {
			List<BlockContainerJS> list = new ArrayList<>(explosion.getToBlow().size());

			for (var pos : explosion.getToBlow()) {
				list.add(new BlockContainerJS(level, pos));
			}

			return list;
		}

		public void removeAffectedBlock(BlockContainerJS block) {
			explosion.getToBlow().remove(block.getPos());
		}

		public void removeAllAffectedBlocks() {
			explosion.getToBlow().clear();
		}

		public void removeKnockback() {
			explosion.getHitPlayers().clear();
		}
	}
}