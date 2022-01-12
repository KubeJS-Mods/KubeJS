package dev.latvian.mods.kubejs.level.gen.filter.mob;

import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;

public record CategoryFilter(MobCategory category) implements MobFilter {
	@Override
	public boolean test(MobCategory cat, MobSpawnSettings.SpawnerData data) {
		return cat == category;
	}
}
