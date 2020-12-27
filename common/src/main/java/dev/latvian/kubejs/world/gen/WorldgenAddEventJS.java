package dev.latvian.kubejs.world.gen;

import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.util.MapJS;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RangeDecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.ChanceDecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;
import net.minecraft.world.level.levelgen.structure.templatesystem.AlwaysTrueTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;

/**
 * @author LatvianModder
 */
public class WorldgenAddEventJS extends EventJS
{
	/*
	AnyRuleTest ruleTest = new AnyRuleTest();
		ruleTest.list.add(new BlockMatchRuleTest(Blocks.DIRT));
		BlockState ore = Blocks.GLOWSTONE.defaultBlockState();
		int cluster_count = 17;

		BiomeGenerationSettingsBuilder gen = event.getGeneration();
		gen.addFeature(
				GenerationStage.Decoration.UNDERGROUND_ORES,
				Feature.ORE.configured(
						new OreFeatureConfig(
								ruleTest,
								ore,
								cluster_count
						)
				)
						.range(128)
						.squared()
						.count(20)
		);
	 */

	public static BlockState parseBlockState(String blockStateId)
	{
		int i = blockStateId.indexOf('[');

		if (i == -1)
		{
			return Registry.BLOCK.get(new ResourceLocation(blockStateId)).defaultBlockState();
		}
		else if (blockStateId.indexOf(']') == blockStateId.length() - 1)
		{
			String[] s = blockStateId.substring(i + 1, blockStateId.length() - 1).split(",");

			System.out.println(s);
		}

		return Blocks.AIR.defaultBlockState();
	}

	private final BiomeGenerationSettings.Builder builder;

	public WorldgenAddEventJS(BiomeGenerationSettings.Builder b)
	{
		builder = b;
	}

	public RuleTest parseRuleTest(Object o)
	{
		if (Boolean.TRUE.equals(o))
		{
			return AlwaysTrueTest.INSTANCE;
		}
		else if (Boolean.FALSE.equals(o))
		{
			throw new RuntimeException("wtf are you doing");
		}

		return AlwaysTrueTest.INSTANCE;
	}

	private static <T> T cast(Object o)
	{
		return (T) o;
	}

	public void ore(String blockStateId, Object p)
	{
		BlockState state = parseBlockState(blockStateId);

		if (state == Blocks.AIR.defaultBlockState())
		{
			return;
		}

		OreProperties properties = new OreProperties(MapJS.of(p));

		ConfiguredFeature<OreConfiguration, ?> oreConfig = (properties.noSurface ? Feature.NO_SURFACE_ORE : Feature.ORE).configured(new OreConfiguration(properties.spawnsIn, state, properties.clusterCount));

		oreConfig = cast(oreConfig.decorated(FeatureDecorator.RANGE.configured(new RangeDecoratorConfiguration(properties.minHeight, 0, properties.maxHeight))));
		oreConfig = cast(oreConfig.count(properties.clusterCount));

		if (properties.squared)
		{
			oreConfig = cast(oreConfig.squared());
		}

		builder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, oreConfig);
	}

	public void lake(String blockStateId, int rarity)
	{
		BlockState state = parseBlockState(blockStateId);

		if (state == Blocks.AIR.defaultBlockState())
		{
			return;
		}

		builder.addFeature(GenerationStep.Decoration.LAKES, Feature.LAKE.configured(new BlockStateConfiguration(state)).decorated(FeatureDecorator.WATER_LAKE.configured(new ChanceDecoratorConfiguration(rarity))));
	}
}