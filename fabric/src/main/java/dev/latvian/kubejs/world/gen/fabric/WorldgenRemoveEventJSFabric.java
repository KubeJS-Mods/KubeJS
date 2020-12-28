package dev.latvian.kubejs.world.gen.fabric;

import dev.latvian.kubejs.world.gen.WorldgenEntryList;
import dev.latvian.kubejs.world.gen.WorldgenRemoveEventJS;
import net.fabricmc.fabric.api.biome.v1.BiomeModificationContext;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

/**
 * @author LatvianModder
 */
public class WorldgenRemoveEventJSFabric extends WorldgenRemoveEventJS
{
	private final BiomeSelectionContext selectionContext;
	private final BiomeModificationContext modificationContext;

	public WorldgenRemoveEventJSFabric(BiomeSelectionContext s, BiomeModificationContext m)
	{
		selectionContext = s;
		modificationContext = m;
	}

	@Override
	public boolean verifyBiomes(WorldgenEntryList<String> biomes)
	{
		return biomes.verify(s -> {
			if (s.startsWith("#"))
			{
				return selectionContext.getBiome().getBiomeCategory() == Biome.BiomeCategory.byName(s.substring(1));
			}

			return selectionContext.getBiomeKey() == ResourceKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(s));
		});
	}
}
