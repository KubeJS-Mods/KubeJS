package dev.latvian.mods.kubejs.level.gen.filter.mob;

import net.minecraft.core.Registry;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;

import java.util.regex.Pattern;

public record RegexIDFilter(Pattern pattern) implements MobFilter {
	@Override
	public boolean test(MobCategory cat, MobSpawnSettings.SpawnerData data) {
		return pattern.matcher(Registry.ENTITY_TYPE.getKey(data.type).toString()).find();
	}
}
