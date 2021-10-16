package dev.latvian.kubejs.world.gen.fabric;

import dev.latvian.kubejs.util.ConsoleJS;
import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.kubejs.world.gen.RemoveSpawnsByCategoryProperties;
import dev.latvian.kubejs.world.gen.RemoveSpawnsByIDProperties;
import dev.latvian.kubejs.world.gen.WorldgenEntryList;
import dev.latvian.kubejs.world.gen.WorldgenRemoveEventJS;
import net.fabricmc.fabric.api.biome.v1.BiomeModificationContext;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @author LatvianModder
 */
public class WorldgenRemoveEventJSFabric extends WorldgenRemoveEventJS {
	private final BiomeSelectionContext selectionContext;
	private final BiomeModificationContext modificationContext;
	private Registry<ConfiguredFeature<?, ?>> featureRegistry;

	public WorldgenRemoveEventJSFabric(BiomeSelectionContext s, BiomeModificationContext m) {
		selectionContext = s;
		modificationContext = m;
	}

	private Registry<ConfiguredFeature<?, ?>> getFeatureRegistry() {
		if (featureRegistry == null) {
			try {
				Class<?> c = Class.forName("net.fabricmc.fabric.impl.biome.modification.BiomeModificationContextImpl$GenerationSettingsContextImpl");
				Field field = c.getDeclaredField("features");
				field.setAccessible(true);
				featureRegistry = UtilsJS.cast(field.get(modificationContext.getGenerationSettings()));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		return featureRegistry;
	}

	@Override
	@Nullable
	public ResourceLocation getConfiguredFeatureKey(ConfiguredFeature<?, ?> feature) {
		return getFeatureRegistry().getKey(feature);
	}

	@Override
	protected boolean verifyBiomes(WorldgenEntryList biomes) {
		return biomes.verify(s -> {
			if (s.startsWith("#")) {
				return selectionContext.getBiome().getBiomeCategory().getName().equals(s.substring(1));
			}

			return selectionContext.getBiomeKey().location().equals(new ResourceLocation(s));
		});
	}

	@Override
	protected void removeFeature(GenerationStep.Decoration decoration, Predicate<FeatureConfiguration> predicate) {
		for (ConfiguredFeature<?, ?> feature : getFeatureRegistry()) {
			if (checkTree(feature, predicate)) {
				getFeatureRegistry().getResourceKey(feature).ifPresent(key -> modificationContext.getGenerationSettings().removeFeature(decoration, key));
			}
		}
	}

	@Override
	protected void removeSpawn(RemoveSpawnsByCategoryProperties p) {
		modificationContext.getSpawnSettings().removeSpawns((category, spawnerData) -> p.categories.verifyIgnoreCase(category.getName()));
	}

	@Override
	protected void removeSpawn(RemoveSpawnsByIDProperties p) {
		modificationContext.getSpawnSettings().removeSpawns((category, spawnerData) -> p.entities.verify(spawnerData.type.getDescriptionId()));
	}

	@Override
	public void printFeatures(@Nullable GenerationStep.Decoration type) {
		if (type == null) {
			for (GenerationStep.Decoration decoration : GenerationStep.Decoration.values()) {
				printFeatures(decoration);
			}
		} else {
			ConsoleJS.STARTUP.info("Features with type '" + type.name().toLowerCase() + "' in biome '" + selectionContext.getBiomeKey().location() + "':");
			int unknown = 0;

			List<List<Supplier<ConfiguredFeature<?, ?>>>> list = selectionContext.getBiome().getGenerationSettings().features();

			if (list.size() > type.ordinal()) {
				for (Supplier<ConfiguredFeature<?, ?>> cfs : list.get(type.ordinal())) {
					ResourceLocation id = getConfiguredFeatureKey(cfs.get());

					if (id == null) {
						unknown++;
					} else {
						ConsoleJS.STARTUP.info("- " + id);
					}
				}
			}

			if (unknown > 0) {
				ConsoleJS.STARTUP.info("- " + unknown + " features with unknown id");
			}
		}
	}

	@Override
	public void removeFeatureById(GenerationStep.Decoration type, ResourceLocation[] ids) {
		for (ResourceLocation id : ids) {
			modificationContext.getGenerationSettings().removeFeature(type, ResourceKey.create(Registry.CONFIGURED_FEATURE_REGISTRY, id));
		}
	}

	@Override
	public void removeAllFeatures(GenerationStep.Decoration type) {
		for (ResourceLocation key : getFeatureRegistry().keySet()) {
			modificationContext.getGenerationSettings().removeFeature(type, ResourceKey.create(Registry.CONFIGURED_FEATURE_REGISTRY, key));
		}
	}

	@Override
	public void removeAllFeatures() {
		GenerationStep.Decoration[] decorations = GenerationStep.Decoration.values();

		for (ResourceLocation key : getFeatureRegistry().keySet()) {
			for (GenerationStep.Decoration decoration : decorations) {
				modificationContext.getGenerationSettings().removeFeature(decoration, ResourceKey.create(Registry.CONFIGURED_FEATURE_REGISTRY, key));
			}
		}
	}

	@Override
	public void printSpawns(@Nullable MobCategory category) {
		if (category == null) {
			for (MobCategory c : MobCategory.values()) {
				printSpawns(c);
			}

			return;
		}

		ConsoleJS.STARTUP.info("Mod spawns with type '" + category.getName() + "' in biome '" + selectionContext.getBiomeKey().location() + "':");

		for (MobSpawnSettings.SpawnerData data : selectionContext.getBiome().getMobSettings().getMobs(category).unwrap()) {
			ConsoleJS.STARTUP.info("- " + data.toString());
		}
	}

	@Override
	public void removeAllSpawns() {
		modificationContext.getSpawnSettings().clearSpawns();
	}
}
