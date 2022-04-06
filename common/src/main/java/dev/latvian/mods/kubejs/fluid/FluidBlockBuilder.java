package dev.latvian.mods.kubejs.fluid;

import dev.latvian.mods.kubejs.block.BlockBuilder;
import dev.latvian.mods.kubejs.generator.AssetJsonGenerator;
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
		return KubeJSFluidHelper.buildFluidBlock(fluidBuilder, Block.Properties.of(Material.WATER).noCollission().strength(100.0F).noDrops());
	}

	@Override
	public void generateAssetJsons(AssetJsonGenerator generator) {
		generator.blockState(id, m -> m.variant("", id.getNamespace() + ":block/" + id.getPath()));
		generator.blockModel(id, m -> m.texture("particle", fluidBuilder.stillTexture.toString()));
	}
}
