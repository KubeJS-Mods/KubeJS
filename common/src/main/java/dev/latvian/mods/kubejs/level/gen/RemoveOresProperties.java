package dev.latvian.mods.kubejs.level.gen;

import dev.latvian.mods.kubejs.block.state.BlockStatePredicate;
import dev.latvian.mods.kubejs.level.gen.filter.biome.BiomeFilter;
import net.minecraft.world.level.levelgen.GenerationStep;

/**
 * @author LatvianModder
 */
public class RemoveOresProperties {
	public GenerationStep.Decoration worldgenLayer = GenerationStep.Decoration.UNDERGROUND_ORES;
	public BlockStatePredicate blocks = BlockStatePredicate.Simple.NONE;
	public BiomeFilter biomes = BiomeFilter.ALWAYS_TRUE;
}
