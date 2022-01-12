package dev.latvian.mods.kubejs.level.gen;

import dev.latvian.mods.kubejs.event.StartupEventJS;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * FIXME: Move to {@link dev.architectury.registry.level.biome.BiomeModifications} once it's ready.
 *
 * @author LatvianModder
 */
public class WorldgenAddEventJS extends StartupEventJS {
	private static final Pattern SPAWN_PATTERN = Pattern.compile("(\\w+:\\w+)\\*\\((\\d+)-(\\d+)\\):(\\d+)");

	protected void addFeature(GenerationStep.Decoration decoration, ConfiguredFeature<?, ?> configuredFeature) {
	}

	protected void addEntitySpawn(MobCategory category, MobSpawnSettings.SpawnerData spawnerData) {
	}

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

	/*
	public void addOre(Consumer<AddOreProperties> p) {
		AddOreProperties properties = new AddOreProperties();
		p.accept(properties);

		if (properties._block == Blocks.AIR.defaultBlockState()) {
			return;
		}

		if (!verifyBiomes(properties.biomes)) {
			return;
		}

		AnyRuleTest ruleTest = new AnyRuleTest();

		for (var o : ListJS.orSelf(properties.spawnsIn.values)) {
			String s = String.valueOf(o);
			boolean invert = false;

			while (s.startsWith("!")) {
				s = s.substring(1);
				invert = !invert;
			}

			if (s.startsWith("#")) {
				RuleTest tagTest = new TagMatchTest(Tags.blocks().getTag(new ResourceLocation(s.substring(1))));
				ruleTest.list.add(invert ? new InvertRuleTest(tagTest) : tagTest);
			} else {
				BlockState bs = UtilsJS.parseBlockState(s);
				RuleTest tagTest = s.indexOf('[') != -1 ? new BlockStateMatchTest(bs) : new BlockMatchTest(bs.getBlock());
				ruleTest.list.add(invert ? new InvertRuleTest(tagTest) : tagTest);
			}
		}

		RuleTest ruleTest1 = ruleTest.list.isEmpty() ? OreConfiguration.Predicates.NATURAL_STONE : ruleTest;

		ConfiguredFeature<OreConfiguration, ?> oreConfig = (properties.noSurface ? Feature.NO_SURFACE_ORE : Feature.ORE).configured(new OreConfiguration(properties.spawnsIn.blacklist ? new InvertRuleTest(ruleTest1) : ruleTest1, properties._block, properties.clusterMaxSize));

		oreConfig = UtilsJS.cast(oreConfig.decorated(FeatureDecorator.RANGE.configured(new RangeDecoratorConfiguration(properties.minHeight, 0, properties.maxHeight))));
		oreConfig = UtilsJS.cast(oreConfig.count(UniformInt.of(properties.clusterMinCount, properties.clusterMaxCount - properties.clusterMinCount)));

		if (properties.chance > 0) {
			oreConfig = UtilsJS.cast(oreConfig.chance(properties.chance));
		}

		if (properties.squared) {
			oreConfig = UtilsJS.cast(oreConfig.squared());
		}

		addFeature(properties._worldgenLayer, oreConfig);
	}

	public void addLake(Consumer<AddLakeProperties> p) {
		AddLakeProperties properties = new AddLakeProperties();
		p.accept(properties);

		if (properties._block == Blocks.AIR.defaultBlockState()) {
			return;
		}

		if (!verifyBiomes(properties.biomes)) {
			return;
		}

		addFeature(properties._worldgenLayer, Feature.LAKE.configured(new BlockStateConfiguration(properties._block)).decorated((FeatureDecorator.WATER_LAKE).configured(new ChanceDecoratorConfiguration(properties.chance))));
	}

	public void addSpawn(Consumer<AddSpawnProperties> p) {
		AddSpawnProperties properties = new AddSpawnProperties();
		p.accept(properties);

		if (properties._entity == null || properties._category == null) {
			return;
		}

		if (!verifyBiomes(properties.biomes)) {
			return;
		}

		addEntitySpawn(properties._category, new MobSpawnSettings.SpawnerData(properties._entity, properties.weight, properties.minCount, properties.maxCount));
	}

	public void addSpawn(MobCategory category, String spawn) {
		Matcher matcher = SPAWN_PATTERN.matcher(spawn);

		if (matcher.matches()) {
			try {
				addEntitySpawn(category, new MobSpawnSettings.SpawnerData(Objects.requireNonNull(KubeJSRegistries.entityTypes().get(new ResourceLocation(matcher.group(1)))), Integer.parseInt(matcher.group(4)), Integer.parseInt(matcher.group(2)), Integer.parseInt(matcher.group(3))));
			} catch (Exception ex) {
				ConsoleJS.STARTUP.info("Failed to add spawn: " + ex);
			}
		} else {
			ConsoleJS.STARTUP.info("Invalid spawn syntax! Must be mod:entity_type*(minCount-maxCount):weight");
		}

		//minecraft:ghast*(4-4):50
	}
	*/
}