package dev.latvian.mods.kubejs.level.gen.filter.biome;

import dev.architectury.registry.level.biome.BiomeModifications;
import net.minecraft.world.level.biome.Biome;

public record CategoryFilter(Biome.BiomeCategory category) implements BiomeFilter {
	@Override
	public boolean test(BiomeModifications.BiomeContext ctx) {
		return ctx.getProperties().getCategory().equals(category);
	}
}
