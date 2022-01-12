package dev.latvian.mods.kubejs.level.gen.filter.mob;

import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;

import java.util.List;

public record AndFilter(List<MobFilter> list) implements MobFilter {
	@Override
	public boolean test(MobCategory cat, MobSpawnSettings.SpawnerData data) {
		for (MobFilter p : list) {
			if (!p.test(cat, data)) {
				return false;
			}
		}

		return true;
	}
}
