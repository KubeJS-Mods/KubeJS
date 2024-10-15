package dev.latvian.mods.kubejs.entity;

import com.mojang.datafixers.util.Either;
import dev.latvian.mods.kubejs.level.BlockContainerJS;
import dev.latvian.mods.kubejs.level.SpawnerJS;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Info("""
	Invoked before an entity is spawned into the world.
	
	Only entities from a `BaseSpawner` or world generation will trigger this event.
	""")
public class CheckLivingEntitySpawnKubeEvent implements KubeLivingEntityEvent {
	private final LivingEntity entity;
	private final Level level;

	public final double x, y, z;
	public final transient MobSpawnType type;
	private final Either<BlockEntity, Entity> spawnerEither;

	@Nullable
	public transient SpawnerJS spawner;

	public CheckLivingEntitySpawnKubeEvent(LivingEntity entity, Level level, double x, double y, double z, MobSpawnType type, Either<BlockEntity, Entity> spawnerEither) {
		this.entity = entity;
		this.level = level;
		this.x = x;
		this.y = y;
		this.z = z;
		this.type = type;
		this.spawnerEither = spawnerEither;
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
		return new BlockContainerJS(level, BlockPos.containing(x, y, z));
	}

	@Info("The type of spawn.")
	public MobSpawnType getType() {
		return type;
	}

	@Info("The spawner that spawned the entity.")
	@NotNull
	public SpawnerJS getSpawner() {
		if (spawner == null) {
			spawner = SpawnerJS.of(spawnerEither);
		}

		return spawner;
	}
}