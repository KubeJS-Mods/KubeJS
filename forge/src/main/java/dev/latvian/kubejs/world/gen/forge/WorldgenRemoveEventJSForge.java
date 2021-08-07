package dev.latvian.kubejs.world.gen.forge;

import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.world.gen.RemoveSpawnsByCategoryProperties;
import dev.latvian.kubejs.world.gen.RemoveSpawnsByIDProperties;
import dev.latvian.kubejs.world.gen.WorldgenEntryList;
import dev.latvian.kubejs.world.gen.WorldgenRemoveEventJS;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @author LatvianModder
 */
public class WorldgenRemoveEventJSForge extends WorldgenRemoveEventJS {
	private final BiomeLoadingEvent event;
	private Registry<ConfiguredFeature<?, ?>> featureRegistry;

	public WorldgenRemoveEventJSForge(BiomeLoadingEvent e) {
		event = e;
	}

	private Registry<ConfiguredFeature<?, ?>> getFeatureRegistry() {
		if (featureRegistry == null) {
			/*
			try {
				Class<?> c = Class.forName("net.fabricmc.fabric.impl.biome.modification.BiomeModificationContextImpl$GenerationSettingsContextImpl");
				Field field = c.getDeclaredField("features");
				field.setAccessible(true);
				featureRegistry = UtilsJS.cast(field.get(modificationContext.getGenerationSettings()));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			 */
			featureRegistry = BuiltinRegistries.CONFIGURED_FEATURE;
		}

		return featureRegistry;
	}

	@Override
	protected boolean verifyBiomes(WorldgenEntryList biomes) {
		return biomes.verify(s -> {
			if (s.startsWith("#")) {
				return event.getCategory() == Biome.BiomeCategory.byName(s.substring(1));
			}

			return new ResourceLocation(s).equals(event.getName());
		});
	}

	@Override
	protected void removeFeature(GenerationStep.Decoration decoration, Predicate<FeatureConfiguration> predicate) {
		event.getGeneration().getFeatures(decoration).removeIf(configuredFeatureSupplier -> checkTree(configuredFeatureSupplier.get(), predicate));
	}

	@Override
	protected void removeSpawn(RemoveSpawnsByCategoryProperties properties) {
		event.getSpawns().getSpawnerTypes().forEach(category -> {
			if (properties.categories.verifyIgnoreCase(category.getName())) {
				event.getSpawns().getSpawner(category).clear();
			}
		});
	}

	@Override
	protected void removeSpawn(RemoveSpawnsByIDProperties properties) {
		event.getSpawns().getSpawnerTypes().forEach(category -> event.getSpawns().getSpawner(category).removeIf(spawner -> properties.entities.verify(spawner.type.getDescriptionId())));
	}

	@Override
	public void printFeatures(@Nullable GenerationStep.Decoration type) {
		if (type == null) {
			for (GenerationStep.Decoration decoration : GenerationStep.Decoration.values()) {
				printFeatures(decoration);
			}
		} else {
			ScriptType.STARTUP.console.info("Features with type '" + type.name().toLowerCase() + "' in biome '" + event.getName() + "':");
			int unknown = 0;

			for (Supplier<ConfiguredFeature<?, ?>> cfs : event.getGeneration().getFeatures(type)) {
				ConfiguredFeature<?, ?> cf = cfs.get();
				ResourceLocation id = getFeatureRegistry().getKey(cf);

				if (id == null) {
					unknown++;
				} else {
					ScriptType.STARTUP.console.info("- " + id);
				}
			}

			if (unknown > 0) {
				ScriptType.STARTUP.console.info("- " + unknown + " features with unknown id");
			}
		}
	}

	public void removeFeatureById(GenerationStep.Decoration type, ResourceLocation id) {
		event.getGeneration().getFeatures(type).removeIf(cf -> id.equals(getFeatureRegistry().getKey(cf.get())));
	}

	@Override
	public void removeAllFeatures(GenerationStep.Decoration type) {
		event.getGeneration().getFeatures(type).clear();
	}

	@Override
	public void removeAllFeatures() {
		for (GenerationStep.Decoration decoration : GenerationStep.Decoration.values()) {
			event.getGeneration().getFeatures(decoration).clear();
		}
	}

	@Override
	public void printSpawns(MobCategory category) {
		ScriptType.STARTUP.console.info("Mod spawns with type '" + category.getName() + "' in biome '" + event.getName() + "':");

		for (MobSpawnSettings.SpawnerData data : event.getSpawns().getSpawner(category)) {
			ScriptType.STARTUP.console.info("- " + data.toString());
		}
	}

	@Override
	public void removeAllSpawns() {
		event.getSpawns().getSpawnerTypes().forEach(category -> event.getSpawns().getSpawner(category).clear());
	}
}
