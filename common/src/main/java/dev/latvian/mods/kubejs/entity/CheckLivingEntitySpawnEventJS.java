package dev.latvian.mods.kubejs.entity;

import dev.latvian.mods.kubejs.level.BlockContainerJS;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;

@Info("""
	Invoked before an entity is spawned into the world.
			
	Only entities from a `BaseSpawner` or world generation will trigger this event.
	""")
public class CheckLivingEntitySpawnEventJS extends LivingEntityEventJS {
	private final LivingEntity entity;
	private final Level level;

	public final double x, y, z;
	public final MobSpawnType type;
	public final BaseSpawner spawner;

	public CheckLivingEntitySpawnEventJS(LivingEntity entity, Level level, double x, double y, double z, MobSpawnType type, BaseSpawner spawner) {
		this.entity = entity;
		this.level = level;
		this.x = x;
		this.y = y;
		this.z = z;
		this.type = type;
		this.spawner = spawner;
	}

	@Override
	@Info("The level the entity is being spawned into.")
	public Level getLevel() {
		return level;
	}

	@Override
	@Info("The entity being spawned.")
	public LivingEntity getEntity() {
		return entity;
	}

	@Info("The block the entity is being spawned on.")
	public BlockContainerJS getBlock() {
		return new BlockContainerJS(level, new BlockPos(x, y, z));
	}

	@Info("The type of spawn.")
	public MobSpawnType getType() {
		return type;
	}

	@Info("The spawner that spawned the entity.")
	public BaseSpawner getSpawner() {
		return spawner;
	}
}