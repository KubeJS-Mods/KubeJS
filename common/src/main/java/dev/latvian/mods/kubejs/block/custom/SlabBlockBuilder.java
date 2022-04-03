package dev.latvian.mods.kubejs.block.custom;

import dev.latvian.mods.kubejs.generator.AssetJsonGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;

public class SlabBlockBuilder extends ShapedBlockBuilder {
	public SlabBlockBuilder(ResourceLocation i) {
		super(i, "_slab");
		tagBoth(BlockTags.SLABS.location());
	}

	@Override
	public Block createObject() {
		return new SlabBlock(createProperties());
	}

	@Override
	public void generateAssetJsons(AssetJsonGenerator generator) {
		generator.blockState(id, bs -> {
			bs.variant("type=double", v -> v.model(newID("block/", "_double").toString()));
			bs.variant("type=bottom", v -> v.model(newID("block/", "_bottom").toString()));
			bs.variant("type=top", v -> v.model(newID("block/", "_top").toString()));
		});

		final var texture = textures.get("texture").getAsString();

		generator.blockModel(newID("", "_double"), m -> {
			m.parent("minecraft:block/cube_all");
			m.texture("all", texture);
		});

		generator.blockModel(newID("", "_bottom"), m -> {
			m.parent("minecraft:block/slab");
			m.texture("bottom", texture);
			m.texture("top", texture);
			m.texture("side", texture);
		});

		generator.blockModel(newID("", "_top"), m -> {
			m.parent("minecraft:block/slab_top");
			m.texture("bottom", texture);
			m.texture("top", texture);
			m.texture("side", texture);
		});

		generator.itemModel(itemBuilder.id, m -> m.parent(newID("block/", "_bottom").toString()));
	}
}
