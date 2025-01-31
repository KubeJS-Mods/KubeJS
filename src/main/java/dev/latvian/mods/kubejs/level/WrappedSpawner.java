package dev.latvian.mods.kubejs.level;

import com.mojang.datafixers.util.Either;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public record WrappedSpawner(@Nullable Entity entity, @Nullable LevelBlock block) {
	public static WrappedSpawner of(Either<BlockEntity, Entity> spawner) {
		if (spawner == null) {
			return new WrappedSpawner(null, null);
		}

		var e = spawner.right().orElse(null);

		if (e != null) {
			return new WrappedSpawner(e, null);
		}

		var be = spawner.left().orElse(null);

		if (be != null) {
			return new WrappedSpawner(null, be.getLevel().kjs$getBlock(be));
		}

		return new WrappedSpawner(null, null);
	}

	public boolean isWorldgen() {
		return entity == null && block == null;
	}
}
