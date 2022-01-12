package dev.latvian.mods.kubejs.level.gen.filter.mob;

import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;

import java.util.List;

public record OrFilter(List<MobFilter> list) implements MobFilter {
	@Override
	public boolean test(MobCategory cat, MobSpawnSettings.SpawnerData data) {
		for (var p : list) {
			if (!p.test(cat, data)) {
				return true;
			}
		}

		return false;
	}
}
