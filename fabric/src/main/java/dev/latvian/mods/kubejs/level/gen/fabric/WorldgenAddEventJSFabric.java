package dev.latvian.mods.kubejs.level.gen.fabric;

import dev.latvian.mods.kubejs.level.gen.WorldgenAddEventJS;
import dev.latvian.mods.kubejs.level.gen.WorldgenEntryList;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.fabricmc.fabric.api.biome.v1.BiomeModificationContext;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

import java.util.UUID;

/**
 * @author LatvianModder
 */
public class WorldgenAddEventJSFabric extends WorldgenAddEventJS {
	private final BiomeSelectionContext selectionContext;
	private final BiomeModificationContext modificationContext;
	private Registry<ConfiguredFeature<?, ?>> featureRegistry;

	public WorldgenAddEventJSFabric(BiomeSelectionContext s, BiomeModificationContext m) {
		selectionContext = s;
		modificationContext = m;
	}

	private Registry<ConfiguredFeature<?, ?>> getFeatureRegistry() {
		if (featureRegistry == null) {
			try {
				var c = Class.forName("net.fabricmc.fabric.impl.biome.modification.BiomeModificationContextImpl$GenerationSettingsContextImpl");
				var field = c.getDeclaredField("features");
				field.setAccessible(true);
				featureRegistry = UtilsJS.cast(field.get(modificationContext.getGenerationSettings()));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		return featureRegistry;
	}

	@Override
	protected void addFeature(GenerationStep.Decoration decoration, ConfiguredFeature<?, ?> configuredFeature) {
		try {
			var reg = getFeatureRegistry();
			var id = new ResourceLocation("kubejs", "feature_" + UUID.randomUUID().toString().replace("-", "_").toLowerCase());
			Registry.register(reg, id, configuredFeature);
			// FIXME
			// modificationContext.getGenerationSettings().addFeature(decoration, ResourceKey.create(Registry.CONFIGURED_FEATURE_REGISTRY, id));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	protected void addEntitySpawn(MobCategory category, MobSpawnSettings.SpawnerData spawnerData) {
		modificationContext.getSpawnSettings().addSpawn(category, spawnerData);
	}

	@Override
	protected boolean verifyBiomes(WorldgenEntryList biomes) {
		return biomes.verify(s -> {
			if (s.startsWith("#")) {
				return selectionContext.getBiome().getBiomeCategory() == Biome.BiomeCategory.byName(s.substring(1));
			}

			return selectionContext.getBiomeKey().location().equals(new ResourceLocation(s));
		});
	}
}
