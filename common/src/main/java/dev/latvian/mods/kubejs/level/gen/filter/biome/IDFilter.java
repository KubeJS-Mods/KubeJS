package dev.latvian.mods.kubejs.level.gen.filter.biome;

import dev.architectury.registry.level.biome.BiomeModifications;
import net.minecraft.resources.ResourceLocation;

public record IDFilter(ResourceLocation id) implements BiomeFilter {
	@Override
	public boolean test(BiomeModifications.BiomeContext ctx) {
		return ctx.getKey().equals(id);
	}
}
