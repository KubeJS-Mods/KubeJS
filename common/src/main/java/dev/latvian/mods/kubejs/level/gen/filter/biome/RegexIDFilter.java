package dev.latvian.mods.kubejs.level.gen.filter.biome;

import dev.architectury.registry.level.biome.BiomeModifications;

import java.util.regex.Pattern;

public record RegexIDFilter(Pattern pattern) implements BiomeFilter {
	@Override
	public boolean test(BiomeModifications.BiomeContext ctx) {
		return pattern.matcher(ctx.getKey().toString()).find();
	}
}
