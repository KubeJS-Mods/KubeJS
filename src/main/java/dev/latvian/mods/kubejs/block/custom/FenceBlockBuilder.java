package dev.latvian.mods.kubejs.block.custom;

import dev.latvian.mods.kubejs.client.ModelGenerator;
import dev.latvian.mods.kubejs.client.MultipartBlockStateGenerator;
import dev.latvian.mods.kubejs.generator.KubeAssetGenerator;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceBlock;
import net.neoforged.neoforge.common.Tags;

public class FenceBlockBuilder extends ShapedBlockBuilder {
	public static final Identifier[] FENCE_TAGS = {
		BlockTags.FENCES.location(),
		Tags.Blocks.FENCES.location(),
	};

	private static final Identifier SIDE_MODEL = Identifier.withDefaultNamespace("block/fence_side");
	private static final Identifier POST_MODEL = Identifier.withDefaultNamespace("block/fence_post");
	private static final Identifier INVENTORY_MODEL = Identifier.withDefaultNamespace("block/fence_inventory");

	public FenceBlockBuilder(Identifier i) {
		super(i, "_fence");
		tagBoth(FENCE_TAGS);
	}

	@Override
	public Block createObject() {
		return new FenceBlock(createProperties());
	}

	@Override
	protected boolean useMultipartBlockState() {
		return true;
	}

	@Override
	protected void generateMultipartBlockState(MultipartBlockStateGenerator bs) {
		var modPost = newID("block/", "_post");
		var modSide = newID("block/", "_side");

		bs.part("", modPost);
		bs.part("north=true", p -> p.model(modSide).uvlock());
		bs.part("east=true", p -> p.model(modSide).uvlock().y(90));
		bs.part("south=true", p -> p.model(modSide).uvlock().y(180));
		bs.part("west=true", p -> p.model(modSide).uvlock().y(270));
	}

	@Override
	protected void generateItemModel(ModelGenerator m) {
		m.parent(INVENTORY_MODEL);
		m.texture("texture", baseTexture);
	}

	@Override
	protected void generateBlockModels(KubeAssetGenerator generator) {
		generator.blockModel(newID("", "_post"), m -> {
			m.parent(POST_MODEL);
			m.texture("texture", baseTexture);
		});
		generator.blockModel(newID("", "_side"), m -> {
			m.parent(SIDE_MODEL);
			m.texture("texture", baseTexture);
		});
	}
}
