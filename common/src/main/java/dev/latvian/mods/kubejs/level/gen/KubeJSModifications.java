package dev.latvian.mods.kubejs.level.gen;

import dev.architectury.hooks.level.biome.BiomeProperties;
import dev.architectury.registry.level.biome.BiomeModifications;

import java.util.Collection;
import java.util.HashSet;
import java.util.function.BiConsumer;

public class KubeJSModifications {
	final Collection<BiConsumer<BiomeModifications.BiomeContext, BiomeProperties.Mutable>> ADDITIONS = new HashSet<>();
	final Collection<BiConsumer<BiomeModifications.BiomeContext, BiomeProperties.Mutable>> REMOVALS = new HashSet<>();
	final Collection<BiConsumer<BiomeModifications.BiomeContext, BiomeProperties.Mutable>> REPLACEMENTS = new HashSet<>();
	final Collection<BiConsumer<BiomeModifications.BiomeContext, BiomeProperties.Mutable>> LATE_MODIFICATIONS = new HashSet<>();

	public KubeJSModifications() {
		BiomeModifications.addProperties(this::onAdd);
		BiomeModifications.removeProperties(this::onRemove);
		BiomeModifications.replaceProperties(this::onReplace);
		BiomeModifications.postProcessProperties(this::onLateModification);
	}

	public void onAdd(BiomeModifications.BiomeContext context, BiomeProperties.Mutable properties) {
		ADDITIONS.forEach(c -> c.accept(context, properties));
	}

	public void onRemove(BiomeModifications.BiomeContext context, BiomeProperties.Mutable properties) {
		REMOVALS.forEach(c -> c.accept(context, properties));
	}

	public void onReplace(BiomeModifications.BiomeContext context, BiomeProperties.Mutable properties) {
		REPLACEMENTS.forEach(c -> c.accept(context, properties));
	}

	public void onLateModification(BiomeModifications.BiomeContext context, BiomeProperties.Mutable properties) {
		LATE_MODIFICATIONS.forEach(c -> c.accept(context, properties));
	}

	public void clear() {
		ADDITIONS.clear();
		REMOVALS.clear();
		REPLACEMENTS.clear();
		LATE_MODIFICATIONS.clear();
	}
}
