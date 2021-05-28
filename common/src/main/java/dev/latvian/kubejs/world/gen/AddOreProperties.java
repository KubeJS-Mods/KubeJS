package dev.latvian.kubejs.world.gen;

import dev.latvian.kubejs.util.UtilsJS;
import dev.latvian.kubejs.world.gen.filter.BiomeFilter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GenerationStep;

/**
 * @author LatvianModder
 */
public class AddOreProperties {
	public GenerationStep.Decoration _worldgenLayer = GenerationStep.Decoration.UNDERGROUND_ORES;
	public BlockState _block = Blocks.AIR.defaultBlockState();
	public boolean noSurface = false;
	public int clusterMinSize = 5;
	public int clusterMaxSize = 9;
	public int clusterMinCount = 20;
	public int clusterMaxCount = 20;
	public int chance = 0;
	public int minHeight = 0;
	public int maxHeight = 64;
	public int retrogen = 0;
	public boolean squared = true;
	// TODO: should this be ALWAYS_TRUE instead?
	public BiomeFilter biomes = BiomeFilter.ALWAYS_FALSE;
	public final WorldgenEntryList spawnsIn = new WorldgenEntryList();

	public void setWorldgenLayer(String id) {
		_worldgenLayer = GenerationStep.Decoration.valueOf(id.toUpperCase());
	}

	public void setBlock(String id) {
		_block = UtilsJS.parseBlockState(id);
	}

	public void setClusterCount(int c) {
		clusterMinCount = c;
		clusterMaxCount = c;
	}
}
