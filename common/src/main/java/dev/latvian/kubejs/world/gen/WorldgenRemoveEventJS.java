package dev.latvian.kubejs.world.gen;

import dev.latvian.kubejs.event.StartupEventJS;
import dev.latvian.kubejs.world.gen.filter.BiomeFilter;
import me.shedaniel.architectury.registry.BiomeModifications;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ReplaceBlockConfiguration;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author LatvianModder
 */
public class WorldgenRemoveEventJS extends StartupEventJS {

	protected static boolean checkTree(ConfiguredFeature<?, ?> configuredFeature, Predicate<FeatureConfiguration> predicate) {
		return predicate.test(configuredFeature.config) || configuredFeature.config.getFeatures().anyMatch(cf -> checkTree(cf, predicate));
	}

	protected void removeFeature(BiomeFilter filter, GenerationStep.Decoration decoration, Predicate<FeatureConfiguration> predicate) {
		BiomeModifications.replaceProperties(filter, (ctx, properties) -> {
			properties.getGenerationProperties()
					.getFeatures()
					.get(decoration.ordinal())
					.removeIf(sup -> checkTree(sup.get(), predicate));
		});
	}

	protected void removeSpawn(BiomeFilter filter, BiPredicate<MobCategory, MobSpawnSettings.SpawnerData> predicate) {
		BiomeModifications.replaceProperties(filter, (ctx, properties) -> {
			properties.getSpawnProperties().removeSpawns(predicate);
		});
	}

	protected void removeSpawn(BiomeFilter filter, RemoveSpawnsByCategoryProperties spawns) {
		removeSpawn(filter, (category, spawnerData) -> spawns.categories.verifyIgnoreCase(category.getName()));
	}

	protected void removeSpawn(BiomeFilter filter, RemoveSpawnsByIDProperties spawns) {
		removeSpawn(filter, (category, spawnerData) -> spawns.entities.verify(spawnerData.type.getDescriptionId()));
	}

	public void removeAllFeatures(String type) {
		removeFeature(BiomeFilter.ALWAYS_TRUE, GenerationStep.Decoration.valueOf(type.toUpperCase()), configuredFeature -> true);
	}

	public void removeAllFeatures() {
		BiomeModifications.replaceProperties((ctx, properties) -> {
			properties.getGenerationProperties()
					.getFeatures()
					.forEach(List::clear);
		});
	}

	public void removeOres(Consumer<RemoveOresProperties> p) {
		RemoveOresProperties properties = new RemoveOresProperties();
		p.accept(properties);

		removeFeature(properties._biomes, properties._worldgenLayer, featureConfiguration -> {
			if (featureConfiguration instanceof OreConfiguration) {
				return properties._blocks.check(((OreConfiguration) featureConfiguration).state);
			} else if (featureConfiguration instanceof ReplaceBlockConfiguration) {
				return properties._blocks.check(((ReplaceBlockConfiguration) featureConfiguration).state);
			}

			return false;
		});
	}

	public void removeSpawnsByCategory(Consumer<RemoveSpawnsByCategoryProperties> p) {
		RemoveSpawnsByCategoryProperties properties = new RemoveSpawnsByCategoryProperties();
		p.accept(properties);

		removeSpawn(properties._biomes, properties);
	}

	public void removeSpawnsByID(Consumer<RemoveSpawnsByIDProperties> p) {
		RemoveSpawnsByIDProperties properties = new RemoveSpawnsByIDProperties();
		p.accept(properties);

		removeSpawn(properties._biomes, properties);
	}

	public void removeAllSpawns() {
		removeSpawn(BiomeFilter.ALWAYS_TRUE, (mobCategory, spawnerData) -> true);
	}

}