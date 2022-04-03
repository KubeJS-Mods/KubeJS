package dev.latvian.mods.kubejs.level.gen.filter.biome.fabric;

import dev.architectury.registry.level.biome.BiomeModifications;
import dev.latvian.mods.kubejs.level.gen.filter.biome.BiomeFilter;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

public record BiomeTagFilter(TagKey<Biome> tag) implements BiomeFilter {
	@Override
	public boolean test(BiomeModifications.BiomeContext ctx) {
		// TODO: This may actually be doable now considering vanilla's new
		//  registry-based tag system, we just need a reference to the registry
		ConsoleJS.STARTUP.error("Biome Tag filters are currently not supported on Fabric, sorry!");
		return false;
	}
}
