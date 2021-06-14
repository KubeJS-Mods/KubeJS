package dev.latvian.kubejs.world.gen;

import dev.latvian.kubejs.world.gen.filter.biome.BiomeFilter;
import dev.latvian.kubejs.world.gen.filter.mob.MobFilter;

/**
 * @author LatvianModder
 */
public class RemoveSpawnsProperties {
	public BiomeFilter _biomes = BiomeFilter.ALWAYS_TRUE;
	public MobFilter _mobs = MobFilter.ALWAYS_TRUE;

	public void setBiomes(Object filter) {
		_biomes = BiomeFilter.of(filter);
	}

	public void setMobs(Object filter) {
		_mobs = MobFilter.of(filter);
	}
}
