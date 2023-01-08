package dev.latvian.mods.kubejs.level.gen.filter.biome;

import dev.architectury.registry.level.biome.BiomeModifications;
import net.minecraft.resources.ResourceLocation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record RegexIDFilter(Pattern pattern) implements BiomeFilter {
	@Override
	public boolean test(BiomeModifications.BiomeContext ctx) {
		return ctx.getKey().map(ResourceLocation::toString).map(pattern::matcher).map(Matcher::find).orElse(false);
	}
}
