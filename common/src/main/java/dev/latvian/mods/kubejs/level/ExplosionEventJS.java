package dev.latvian.mods.kubejs.level;

import dev.architectury.hooks.level.ExplosionHooks;
import dev.latvian.mods.kubejs.entity.EntityJS;
import dev.latvian.mods.kubejs.entity.LivingEntityJS;
import dev.latvian.mods.kubejs.player.EntityArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public abstract class ExplosionEventJS extends LevelEventJS {
	protected final Level level;
	protected final Explosion explosion;

	public ExplosionEventJS(Level level, Explosion explosion) {
		this.level = level;
		this.explosion = explosion;
	}

	@Override
	public LevelJS getLevel() {
		return levelOf(level);
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
	public LivingEntityJS getExploder() {
		return getLevel().getLivingEntity(explosion.getSourceMob());
	}

	public static class Pre extends ExplosionEventJS {
		public Pre(Level level, Explosion explosion) {
			super(level, explosion);
		}

		@Override
		public boolean canCancel() {
			return true;
		}

		public float getSize() {
			return explosion.radius;
		}

		public void setSize(float s) {
			explosion.radius = s;
		}
	}

	public static class Post extends ExplosionEventJS {
		private final List<Entity> affectedEntities;

		public Post(Level level, Explosion explosion, List<Entity> affectedEntities) {
			super(level, explosion);
			this.affectedEntities = affectedEntities;
		}

		public EntityArrayList getAffectedEntities() {
			return new EntityArrayList(getLevel(), affectedEntities);
		}

		public void removeAffectedEntity(EntityJS entity) {
			affectedEntities.remove(entity.minecraftEntity);
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