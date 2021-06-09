package dev.latvian.kubejs.world.gen;

import dev.latvian.kubejs.world.gen.filter.BiomeFilter;

/**
 * @author LatvianModder
 */
public class RemoveSpawnsByIDProperties {
	public final WorldgenEntryList entities = new WorldgenEntryList();

	public BiomeFilter _biomes = BiomeFilter.ALWAYS_TRUE;

	public void setBiomes(Object filter) {
		_biomes = BiomeFilter.of(filter);
	}
}
