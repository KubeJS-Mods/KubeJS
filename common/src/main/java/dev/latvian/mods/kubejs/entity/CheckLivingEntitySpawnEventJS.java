package dev.latvian.mods.kubejs.entity;

import dev.latvian.mods.kubejs.level.BlockContainerJS;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;

public class CheckLivingEntitySpawnEventJS extends LivingEntityEventJS {
	private final LivingEntity entity;
	private final Level level;

	public final double x, y, z;
	public final MobSpawnType type;

	public CheckLivingEntitySpawnEventJS(LivingEntity entity, Level level, double x, double y, double z, MobSpawnType type) {
		this.entity = entity;
		this.level = level;
		this.x = x;
		this.y = y;
		this.z = z;
		this.type = type;
	}

	@Override
	public Level getLevel() {
		return level;
	}

	@Override
	public LivingEntity getEntity() {
		return entity;
	}

	public BlockContainerJS getBlock() {
		return new BlockContainerJS(level, new BlockPos(x, y, z));
	}

	public MobSpawnType getType() {
		return type;
	}
}