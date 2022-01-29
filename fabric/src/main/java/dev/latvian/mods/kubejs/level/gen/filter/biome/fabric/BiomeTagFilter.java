package dev.latvian.mods.kubejs.level.gen.filter.biome.fabric;

import dev.architectury.registry.level.biome.BiomeModifications;
import dev.latvian.mods.kubejs.level.gen.filter.biome.BiomeFilter;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.biome.Biome;

public record BiomeTagFilter(Tag<Biome> tag) implements BiomeFilter {
	@Override
	public boolean test(BiomeModifications.BiomeContext ctx) {
		ConsoleJS.STARTUP.error("Biome Tag filters are currently not supported on Fabric, sorry!");
		// TODO: retrieve biome from BiomeContext somehow,
		//  might be worth a pull request to Arch or something?
		return false;
	}
}
