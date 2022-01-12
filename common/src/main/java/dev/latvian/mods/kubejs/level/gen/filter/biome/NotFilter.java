package dev.latvian.mods.kubejs.level.gen.filter.biome;

import dev.architectury.registry.level.biome.BiomeModifications;

public record NotFilter(BiomeFilter original) implements BiomeFilter {
	@Override
	public boolean test(BiomeModifications.BiomeContext ctx) {
		return !original.test(ctx);
	}
}

