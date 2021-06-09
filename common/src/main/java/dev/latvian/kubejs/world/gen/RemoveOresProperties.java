package dev.latvian.kubejs.world.gen;

import dev.latvian.kubejs.block.BlockStatePredicate;
import dev.latvian.kubejs.world.gen.filter.BiomeFilter;
import net.minecraft.world.level.levelgen.GenerationStep;

/**
 * @author LatvianModder
 */
public class RemoveOresProperties {
	public GenerationStep.Decoration _worldgenLayer = GenerationStep.Decoration.UNDERGROUND_ORES;
	public BlockStatePredicate _blocks = BlockStatePredicate.Empty.INSTANCE;
	public BiomeFilter _biomes = BiomeFilter.ALWAYS_TRUE;

	public void setWorldgenLayer(String id) {
		_worldgenLayer = GenerationStep.Decoration.valueOf(id.toUpperCase());
	}

	public void setBlocks(Object blocks) {
		_blocks = BlockStatePredicate.of(blocks);
	}

	public void setBiomes(Object filter) {
		_biomes = BiomeFilter.of(filter);
	}
}
