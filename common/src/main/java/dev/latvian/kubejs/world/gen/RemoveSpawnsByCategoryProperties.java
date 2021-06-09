package dev.latvian.kubejs.world.gen;

import dev.latvian.kubejs.world.gen.filter.BiomeFilter;

/**
 * @author LatvianModder
 */
public class RemoveSpawnsByCategoryProperties {
	public final WorldgenEntryList categories = new WorldgenEntryList();

	public BiomeFilter _biomes = BiomeFilter.ALWAYS_TRUE;

	public void setBiomes(Object filter) {
		_biomes = BiomeFilter.of(filter);
	}
}
