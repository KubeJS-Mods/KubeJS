package dev.latvian.mods.kubejs.block.custom;

import dev.latvian.mods.kubejs.client.ModelGenerator;
import dev.latvian.mods.kubejs.client.VariantBlockStateGenerator;
import dev.latvian.mods.kubejs.generator.KubeAssetGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;

public class SlabBlockBuilder extends ShapedBlockBuilder {
	public static final ResourceLocation[] SLAB_TAGS = {
		BlockTags.SLABS.location(),
	};

	public SlabBlockBuilder(ResourceLocation i) {
		super(i, "_slab");
		tagBoth(SLAB_TAGS);
	}

	@Override
	public Block createObject() {
		return new SlabBlock(createProperties());
	}

	@Override
	protected void generateBlockStateJson(VariantBlockStateGenerator bs) {
		bs.variant("type=double", v -> v.model(newID("block/", "_double").toString()));
		bs.variant("type=bottom", v -> v.model(newID("block/", "_bottom").toString()));
		bs.variant("type=top", v -> v.model(newID("block/", "_top").toString()));
	}

	@Override
	protected void generateBlockModelJsons(KubeAssetGenerator generator) {
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
	}

	@Override
	protected void generateItemModelJson(ModelGenerator m) {
		m.parent(newID("block/", "_bottom").toString());
	}
}
