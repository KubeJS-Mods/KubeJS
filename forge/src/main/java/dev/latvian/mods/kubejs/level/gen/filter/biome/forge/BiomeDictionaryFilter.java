package dev.latvian.mods.kubejs.level.gen.filter.biome.forge;

import dev.architectury.registry.level.biome.BiomeModifications;
import dev.latvian.mods.kubejs.level.gen.filter.biome.BiomeFilter;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.common.BiomeDictionary;

public record BiomeDictionaryFilter(BiomeDictionary.Type type) implements BiomeFilter {
	@Override
	public boolean test(BiomeModifications.BiomeContext ctx) {
		var key = ResourceKey.create(Registry.BIOME_REGISTRY, ctx.getKey());
		return BiomeDictionary.hasType(key, type);
	}
}
