package dev.latvian.kubejs.world.gen;

import dev.latvian.kubejs.util.MapJS;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;

/**
 * @author LatvianModder
 */
public class OreProperties
{
	public RuleTest spawnsIn = OreConfiguration.Predicates.NATURAL_STONE;
	public boolean noSurface = false;
	public int clusterMinSize = 5;
	public int clusterMaxSize = 9;
	public int clusterCount = 20;
	public int chance = 0;
	public int minHeight = 0;
	public int maxHeight = 64;
	public int retrogen = 0;
	public boolean squared = true;

	public OreProperties(MapJS map)
	{
	}
	
	/*
	public 
	
	conditions: [
	{
		type: 'biome_whitelist',
				value: [
		'minecraft:plains'
          ]
	},
	{
		type: 'dimension_blacklist',
				value: [
		'minecraft:the_end',
				'minecraft:the_nether'
          ]
	}
    ]
	 */
}
