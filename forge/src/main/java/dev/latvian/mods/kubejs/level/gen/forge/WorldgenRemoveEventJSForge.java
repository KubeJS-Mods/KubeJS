/*
package dev.latvian.mods.kubejs.world.gen.forge;

import dev.latvian.mods.kubejs.util.ConsoleJS;
import RemoveSpawnsByCategoryProperties;
import RemoveSpawnsByIDProperties;
import WorldgenEntryList;
import WorldgenRemoveEventJS;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

*/
/**
 * @author LatvianModder
 *//*

public class WorldgenRemoveEventJSForge extends WorldgenRemoveEventJS {
	private final BiomeLoadingEvent event;
	private final Map<ConfiguredFeature<?, ?>, Optional<ResourceLocation>> featureRegistry = new HashMap<>();

	public WorldgenRemoveEventJSForge(BiomeLoadingEvent e) {
		event = e;
	}

	@Override
	@Nullable
	public ResourceLocation getConfiguredFeatureKey(ConfiguredFeature<?, ?> feature) {
		return featureRegistry.computeIfAbsent(feature, f -> {
			ResourceLocation id = BuiltinRegistries.CONFIGURED_FEATURE.getKey(f);

			if (id != null) {
				return Optional.of(id);
			}

			// dev.architectury.registry.Registry<ConfiguredFeature<?, ?>> reg = KubeJSRegistries.genericRegistry(Registry.CONFIGURED_FEATURE_REGISTRY);
			// return reg.getKey(f).map(ResourceKey::location);
			return Optional.empty();
		}).orElse(null);
	}

	@Override
	protected boolean verifyBiomes(WorldgenEntryList biomes) {
		return biomes.verify(s -> {
			if (s.startsWith("#")) {
				return event.getCategory().getName().equals(s.substring(1));
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
			ConsoleJS.STARTUP.info("Features with type '" + type.name().toLowerCase() + "' in biome '" + event.getName() + "':");
			int unknown = 0;

			for (Supplier<ConfiguredFeature<?, ?>> cfs : event.getGeneration().getFeatures(type)) {
				ConfiguredFeature<?, ?> cf = cfs.get();
				ResourceLocation id = getConfiguredFeatureKey(cf);

				if (id == null) {
					unknown++;
				} else {
					ConsoleJS.STARTUP.info("- " + id);
				}
			}

			if (unknown > 0) {
				ConsoleJS.STARTUP.info("- " + unknown + " features with unknown id");
			}
		}
	}

	@Override
	public void removeFeatureById(GenerationStep.Decoration type, ResourceLocation[] ids) {
		Set<ResourceLocation> set = Arrays.stream(ids).collect(Collectors.toSet());
		event.getGeneration().getFeatures(type).removeIf(cf -> {
			ResourceLocation id = getConfiguredFeatureKey(cf.get());
			return id != null && set.contains(id);
		});
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
	public void printSpawns(@Nullable MobCategory category) {
		if (category == null) {
			for (MobCategory c : MobCategory.values()) {
				printSpawns(c);
			}

			return;
		}

		ConsoleJS.STARTUP.info("Mod spawns with type '" + category.getName() + "' in biome '" + event.getName() + "':");

		for (MobSpawnSettings.SpawnerData data : event.getSpawns().getSpawner(category)) {
			ConsoleJS.STARTUP.info("- " + data.toString());
		}
	}

	@Override
	public void removeAllSpawns() {
		event.getSpawns().getSpawnerTypes().forEach(category -> event.getSpawns().getSpawner(category).clear());
	}
}
*/
