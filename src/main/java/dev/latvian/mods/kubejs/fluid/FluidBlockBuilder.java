package dev.latvian.mods.kubejs.fluid;

import dev.latvian.mods.kubejs.block.BlockBuilder;
import dev.latvian.mods.kubejs.block.BlockRenderType;
import dev.latvian.mods.kubejs.generator.KubeAssetGenerator;
import dev.latvian.mods.kubejs.item.ItemBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public class FluidBlockBuilder extends BlockBuilder {
	public final FluidBuilder fluidBuilder;

	public FluidBlockBuilder(FluidBuilder b) {
		super(b.id);
		fluidBuilder = b;
		defaultTranslucent();
		noItem();
		noDrops();
		renderType(BlockRenderType.SOLID);
	}

	@Override
	public Block createObject() {
		var properties = Block.Properties.ofFullCopy(Blocks.WATER).noCollision().strength(100F).noLootTable();
		properties.setId(ResourceKey.create(BuiltInRegistries.BLOCK.key(), this.id));
		return new LiquidBlock(fluidBuilder.get(), properties);
	}

	@Override
	protected void generateBlockModels(KubeAssetGenerator generator) {
		generator.blockModel(id, mg -> {
			mg.parent(null);
			mg.texture("particle", fluidBuilder.fluidType.actualStillTexture.toString());

			if (fluidBuilder.fluidType.renderType != BlockRenderType.SOLID) {
				mg.custom(json -> json.addProperty("render_type", switch (fluidBuilder.fluidType.renderType) {
					case CUTOUT -> "cutout";
					case TRANSLUCENT -> "translucent";
					default -> "solid";
				}));
			}
		});
	}

	@Override
	public BlockBuilder item(@Nullable Consumer<ItemBuilder> i) {
		if (i != null) {
			throw new IllegalStateException("Fluid blocks cannot have items!");
		} else {
			return super.item(null);
		}
	}
}
