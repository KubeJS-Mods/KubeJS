package dev.latvian.kubejs.world.gen;

import dev.latvian.kubejs.world.gen.filter.BiomeFilter;

/**
 * @author LatvianModder
 */
public class RemoveSpawnsByIDProperties {
	public final WorldgenEntryList entities = new WorldgenEntryList();

	public BiomeFilter biomes = BiomeFilter.ALWAYS_TRUE;
}
