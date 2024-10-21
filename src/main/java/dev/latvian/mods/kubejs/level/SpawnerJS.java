package dev.latvian.mods.kubejs.level;

import com.mojang.datafixers.util.Either;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public record SpawnerJS(@Nullable Entity entity, @Nullable BlockContainerJS block) {
	public static SpawnerJS of(Either<BlockEntity, Entity> spawner) {
		if (spawner == null) {
			return new SpawnerJS(null, null);
		}

		var e = spawner.right().orElse(null);

		if (e != null) {
			return new SpawnerJS(e, null);
		}

		var be = spawner.left().orElse(null);

		if (be != null) {
			return new SpawnerJS(null, new BlockContainerJS(be));
		}

		return new SpawnerJS(null, null);
	}

	public boolean isWorldgen() {
		return entity == null && block == null;
	}
}
