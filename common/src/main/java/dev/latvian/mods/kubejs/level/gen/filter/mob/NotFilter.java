package dev.latvian.mods.kubejs.level.gen.filter.mob;

import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;

public record NotFilter(MobFilter original) implements MobFilter {
	@Override
	public boolean test(MobCategory cat, MobSpawnSettings.SpawnerData data) {
		return !original.test(cat, data);
	}
}

