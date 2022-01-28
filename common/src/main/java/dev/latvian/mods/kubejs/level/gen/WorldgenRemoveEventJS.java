package dev.latvian.mods.kubejs.level.gen;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import dev.latvian.mods.kubejs.core.RegistryGetterKJS;
import dev.latvian.mods.kubejs.event.StartupEventJS;
import dev.latvian.mods.kubejs.level.gen.filter.biome.BiomeFilter;
import dev.latvian.mods.kubejs.level.gen.properties.RemoveOresProperties;
import dev.latvian.mods.kubejs.level.gen.properties.RemoveSpawnsProperties;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ReplaceBlockConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static dev.latvian.mods.kubejs.util.UtilsJS.onMatchDo;

/**
 * FIXME: Move to {@link dev.architectury.registry.level.biome.BiomeModifications} once it's ready.
 *
 * @author LatvianModder
 */
public class WorldgenRemoveEventJS extends StartupEventJS {

	private static final KubeJSModifications MODIFICATIONS = new KubeJSModifications();

	private ResourceLocation getId(Supplier<PlacedFeature> feature) {
		// this is the worst, but if we're still decoding things from network,
		// this is our only way to get the ID since the instances don't match
		// up with the BuiltinRegistries ones.
		if (feature instanceof RegistryGetterKJS<?> reg) {
			return reg.getId();
		}
		return BuiltinRegistries.PLACED_FEATURE.getKey(feature.get());
	}

	private <T> List<T> padListAndGet(List<List<T>> features, int i) {
		while (features.size() <= i) {
			features.add(Lists.newArrayList());
		}
		return features.get(i);
	}

	protected static boolean checkTree(ConfiguredFeature<?, ?> configuredFeature, Predicate<FeatureConfiguration> predicate) {
		return predicate.test(configuredFeature.config) || configuredFeature.config.getFeatures().anyMatch(cf -> checkTree(cf, predicate));
	}

	protected static boolean check(PlacedFeature feature, Predicate<FeatureConfiguration> predicate) {
		return feature.getFeatures().anyMatch(cf -> checkTree(cf, predicate));
	}

	public WorldgenRemoveEventJS() {
		MODIFICATIONS.clear();
	}

	private void removeFeature(BiomeFilter filter, GenerationStep.Decoration decoration, Predicate<FeatureConfiguration> predicate) {
		MODIFICATIONS.REPLACEMENTS.add((ctx, properties) -> {
			if (filter.test(ctx)) {
				padListAndGet(properties.getGenerationProperties().getFeatures(), decoration.ordinal())
						.removeIf(onMatchDo(sup -> check(sup.get(), predicate), value -> {
							ConsoleJS.STARTUP.debug("Removing feature %s from generation step %s in biome %s"
									.formatted(getId(value), decoration.name().toLowerCase(), ctx.getKey()));
						}));
			}
		});
	}

	private void removeSpawn(BiomeFilter filter, BiPredicate<MobCategory, MobSpawnSettings.SpawnerData> predicate) {
		MODIFICATIONS.REPLACEMENTS.add((ctx, properties) -> {
			if (filter.test(ctx)) {
				properties.getSpawnProperties().removeSpawns(predicate);
			}
		});
	}

	public void printFeatures(GenerationStep.Decoration type) {
		MODIFICATIONS.ADDITIONS.add((ctx, properties) -> {
			var biome = ctx.getKey();
			var features = padListAndGet(properties.getGenerationProperties().getFeatures(), type.ordinal());

			ConsoleJS.STARTUP.info("Features with type '%s' in biome '%s':".formatted(type.name().toLowerCase(), biome));

			var unknown = 0;

			for (var feature : features) {
				var id = getId(feature);

				if (id == null) {
					unknown++;
				} else {
					ConsoleJS.STARTUP.info("- " + id);
				}
			}

			if (unknown > 0) {
				ConsoleJS.STARTUP.info("- " + unknown + " features with unknown id");
			}
		});
	}

	public void printFeatures() {
		for (var decoration : GenerationStep.Decoration.values()) {
			printFeatures(decoration);
		}
	}

	public void removeFeatureById(BiomeFilter filter, GenerationStep.Decoration decoration, ResourceLocation[] id) {
		var ids = Sets.newHashSet(id);
		MODIFICATIONS.REPLACEMENTS.add((ctx, properties) -> {
			if (filter.test(ctx)) {
				padListAndGet(properties.getGenerationProperties().getFeatures(), decoration.ordinal())
						.removeIf(sup -> ids.contains(getId(sup)));
			}
		});
	}

	public void removeFeatureById(GenerationStep.Decoration type, ResourceLocation[] id) {
		removeFeatureById(BiomeFilter.ALWAYS_TRUE, type, id);
	}

	public void removeAllFeatures(BiomeFilter filter, GenerationStep.Decoration type) {
		removeFeature(filter, type, configuredFeature -> true);
	}

	public void removeAllFeatures(BiomeFilter filter) {
		for (var decoration : GenerationStep.Decoration.values()) {
			removeAllFeatures(filter, decoration);
		}
	}

	public void removeAllFeatures(GenerationStep.Decoration type) {
		removeFeature(BiomeFilter.ALWAYS_TRUE, type, configuredFeature -> true);
	}

	public void removeAllFeatures() {
		for (var decoration : GenerationStep.Decoration.values()) {
			removeAllFeatures(decoration);
		}
	}

	public void removeOres(Consumer<RemoveOresProperties> p) {
		var properties = new RemoveOresProperties();
		p.accept(properties);
		removeFeature(properties.biomes, properties.worldgenLayer, fc -> {
			if (fc instanceof OreConfiguration ore) {
				return properties.blocks.check(ore.targetStates);
			} else if (fc instanceof ReplaceBlockConfiguration rb) {
				return properties.blocks.check(rb.targetStates);
			}

			return false;
		});
	}

	public void printSpawns(@Nullable MobCategory category) {
		MODIFICATIONS.ADDITIONS.add((ctx, properties) -> {
			var biome = ctx.getKey();
			var spawns = properties.getSpawnProperties().getSpawners();

			var cats = category == null ? spawns.keySet() : ImmutableSet.of(category);
			for (var cat : cats) {
				ConsoleJS.STARTUP.info("Mob spawns with type '%s' in biome '%s':".formatted(cat.getName(), biome));
				for (var data : spawns.get(cat)) {
					ConsoleJS.STARTUP.info("- " + data.toString());
				}
			}
		});
	}

	public void printSpawns() {
		printSpawns(null);
	}

	public void removeSpawns(Consumer<RemoveSpawnsProperties> p) {
		var properties = new RemoveSpawnsProperties();
		p.accept(properties);
		removeSpawn(properties.biomes, properties.mobs);
	}

	public void removeAllSpawns() {
		removeSpawn(BiomeFilter.ALWAYS_TRUE, (mobCategory, spawnerData) -> true);
	}
}