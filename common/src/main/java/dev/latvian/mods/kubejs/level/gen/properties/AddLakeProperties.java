package dev.latvian.mods.kubejs.level.gen.properties;

import dev.latvian.mods.kubejs.block.state.BlockStatePredicate;
import dev.latvian.mods.kubejs.level.gen.filter.biome.BiomeFilter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.material.Fluids;

/**
 * @author LatvianModder
 */
public class AddLakeProperties {
	public ResourceLocation id = null;

	public GenerationStep.Decoration worldgenLayer = GenerationStep.Decoration.LAKES;
	public BiomeFilter biomes = BiomeFilter.ALWAYS_TRUE;

	// TODO: (eventually) replace with BlockStateProvider wrapper
	public BlockStatePredicate fluid = BlockStatePredicate.of(Fluids.WATER.getSource().defaultFluidState().createLegacyBlock());
	public BlockStatePredicate barrier = BlockStatePredicate.of(Blocks.STONE.defaultBlockState());

	public int chance = 20;

	public int retrogen = 0;

	public void setInner(BlockStatePredicate p) {
		fluid = p;
	}

	public void setOuter(BlockStatePredicate p) {
		barrier = p;
	}
}
