package dev.latvian.mods.kubejs.world.gen;

import dev.latvian.mods.kubejs.event.StartupEventJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ReplaceBlockConfiguration;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * FIXME: Move to {@link dev.architectury.registry.level.biome.BiomeModifications} once it's ready.
 *
 * @author LatvianModder
 */
public class WorldgenRemoveEventJS extends StartupEventJS {
	protected boolean verifyBiomes(WorldgenEntryList biomes) {
		return true;
	}

	public boolean isInBiomes(String[] filter) {
		var list = new WorldgenEntryList();
		list.blacklist = false;
		list.values.addAll(Arrays.asList(filter));
		return verifyBiomes(list);
	}

	public boolean isNotInBiomes(String[] filter) {
		var list = new WorldgenEntryList();
		list.blacklist = true;
		list.values.addAll(Arrays.asList(filter));
		return verifyBiomes(list);
	}

	protected static boolean checkTree(ConfiguredFeature<?, ?> configuredFeature, Predicate<FeatureConfiguration> predicate) {
		return predicate.test(configuredFeature.config) || configuredFeature.config.getFeatures().anyMatch(cf -> checkTree(cf, predicate));
	}

	@Nullable
	public ResourceLocation getConfiguredFeatureKey(ConfiguredFeature<?, ?> feature) {
		return null;
	}

	protected void removeFeature(GenerationStep.Decoration decoration, Predicate<FeatureConfiguration> predicate) {
	}

	protected void removeSpawn(RemoveSpawnsByCategoryProperties properties) {
	}

	protected void removeSpawn(RemoveSpawnsByIDProperties properties) {
	}

	public void printFeatures(@Nullable GenerationStep.Decoration type) {
	}

	public void printFeatures() {
		printFeatures(null);
	}

	public void removeFeatureById(GenerationStep.Decoration type, ResourceLocation[] id) {
	}

	public void removeAllFeatures(GenerationStep.Decoration type) {
		removeFeature(type, configuredFeature -> true);
	}

	public void removeAllFeatures() {
		for (var decoration : GenerationStep.Decoration.values()) {
			removeFeature(decoration, configuredFeature -> true);
		}
	}

	public void removeOres(Consumer<RemoveOresProperties> p) {
		var properties = new RemoveOresProperties();
		p.accept(properties);

		if (!verifyBiomes(properties.biomes)) {
			return;
		}

		removeFeature(properties._worldgenLayer, featureConfiguration -> {
			if (featureConfiguration instanceof OreConfiguration) {
				return properties.blocks.check(((OreConfiguration) featureConfiguration).targetStates);
			} else if (featureConfiguration instanceof ReplaceBlockConfiguration) {
				return properties.blocks.check(((ReplaceBlockConfiguration) featureConfiguration).targetStates);
			}

			return false;
		});
	}

	public void printSpawns(@Nullable MobCategory category) {
	}

	public void printSpawns() {
		printSpawns(null);
	}

	public void removeSpawnsByCategory(Consumer<RemoveSpawnsByCategoryProperties> p) {
		var properties = new RemoveSpawnsByCategoryProperties();
		p.accept(properties);

		if (verifyBiomes(properties.biomes)) {
			removeSpawn(properties);
		}
	}

	public void removeSpawnsByID(Consumer<RemoveSpawnsByIDProperties> p) {
		var properties = new RemoveSpawnsByIDProperties();
		p.accept(properties);

		if (verifyBiomes(properties.biomes)) {
			removeSpawn(properties);
		}
	}

	public void removeAllSpawns() {
	}
}