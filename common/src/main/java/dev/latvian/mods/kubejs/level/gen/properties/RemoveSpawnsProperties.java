package dev.latvian.mods.kubejs.level.gen.properties;

import dev.latvian.mods.kubejs.level.gen.filter.biome.BiomeFilter;
import dev.latvian.mods.kubejs.level.gen.filter.mob.MobFilter;

public class RemoveSpawnsProperties {
	public BiomeFilter biomes = BiomeFilter.ALWAYS_TRUE;
	public MobFilter mobs = MobFilter.ALWAYS_TRUE;
}
