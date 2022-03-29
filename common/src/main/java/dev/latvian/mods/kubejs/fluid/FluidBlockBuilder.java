package dev.latvian.mods.kubejs.fluid;

import dev.latvian.mods.kubejs.block.BlockBuilder;
import dev.latvian.mods.kubejs.block.KubeJSBlockEventHandler;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;

public class FluidBlockBuilder extends BlockBuilder {
	private final FluidBuilder fluidBuilder;

	public FluidBlockBuilder(FluidBuilder b) {
		super(b.id);
		fluidBuilder = b;
		defaultTranslucent();
	}

	@Override
	public Block createObject() {
		return KubeJSBlockEventHandler.buildFluidBlock(fluidBuilder, Block.Properties.of(Material.WATER).noCollission().strength(100.0F).noDrops());
	}
}
