package dev.latvian.kubejs.world.gen;

import dev.latvian.kubejs.world.gen.filter.BiomeFilter;

/**
 * @author LatvianModder
 */
public class RemoveSpawnsByCategoryProperties {
	public final WorldgenEntryList categories = new WorldgenEntryList();

	public BiomeFilter biomes = BiomeFilter.ALWAYS_TRUE;
}
