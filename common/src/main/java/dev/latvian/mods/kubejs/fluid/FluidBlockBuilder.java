package dev.latvian.mods.kubejs.fluid;

import dev.architectury.core.block.ArchitecturyLiquidBlock;
import dev.latvian.mods.kubejs.block.BlockBuilder;
import dev.latvian.mods.kubejs.block.BlockItemBuilder;
import dev.latvian.mods.kubejs.generator.AssetJsonGenerator;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class FluidBlockBuilder extends BlockBuilder {
	private final FluidBuilder fluidBuilder;

	public FluidBlockBuilder(FluidBuilder b) {
		super(b.id);
		fluidBuilder = b;
		defaultTranslucent();
		noItem();
		noDrops();
	}

	@Override
	public Block createObject() {
		return new ArchitecturyLiquidBlock(UtilsJS.cast(fluidBuilder.flowingFluid), Block.Properties.of(Material.WATER).noCollission().strength(100.0F).noDrops());
	}

	@Override
	public void generateAssetJsons(AssetJsonGenerator generator) {
		generator.blockState(id, m -> m.variant("", id.getNamespace() + ":block/" + id.getPath()));
		generator.blockModel(id, m -> {
			m.parent("");
			m.texture("particle", fluidBuilder.stillTexture.toString());
		});
	}

	@Override
	public BlockBuilder item(@Nullable Consumer<BlockItemBuilder> i) {
		if (i != null) {
			throw new IllegalStateException("Fluid blocks cannot have items!");
		} else {
			return super.item(null);
		}
	}
}
