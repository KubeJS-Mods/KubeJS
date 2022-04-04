package dev.latvian.mods.kubejs.block.custom;

import dev.latvian.mods.kubejs.generator.AssetJsonGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PressurePlateBlock;

public class WoodenPressurePlateBlockBuilder extends ShapedBlockBuilder {
	public WoodenPressurePlateBlockBuilder(ResourceLocation i) {
		super(i, "_wooden_pressure_plate", "_pressure_plate");
		noCollision();
		tagBoth(BlockTags.PRESSURE_PLATES.location());
		tagBoth(BlockTags.WOODEN_PRESSURE_PLATES.location());
	}

	@Override
	public Block createObject() {
		return new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, createProperties());
	}

	@Override
	public void generateAssetJsons(AssetJsonGenerator generator) {
		generator.blockState(id, bs -> {
			bs.variant("powered=true", v -> v.model(newID("block/", "_down").toString()));
			bs.variant("powered=false", v -> v.model(newID("block/", "_up").toString()));
		});

		final var texture = textures.get("texture").getAsString();

		generator.blockModel(newID("", "_down"), m -> {
			m.parent("minecraft:block/pressure_plate_down");
			m.texture("texture", texture);
		});

		generator.blockModel(newID("", "_up"), m -> {
			m.parent("minecraft:block/pressure_plate_up");
			m.texture("texture", texture);
		});

		generator.itemModel(itemBuilder.id, m -> m.parent(newID("block/", "_up").toString()));
	}
}
