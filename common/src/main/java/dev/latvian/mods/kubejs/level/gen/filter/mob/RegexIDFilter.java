package dev.latvian.mods.kubejs.level.gen.filter.mob;

import dev.latvian.mods.kubejs.registry.RegistryInfo;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;

import java.util.regex.Pattern;

public record RegexIDFilter(Pattern pattern) implements MobFilter {
	@Override
	public boolean test(MobCategory cat, MobSpawnSettings.SpawnerData data) {
		return pattern.matcher(RegistryInfo.ENTITY_TYPE.getId(data.type).toString()).find();
	}
}
