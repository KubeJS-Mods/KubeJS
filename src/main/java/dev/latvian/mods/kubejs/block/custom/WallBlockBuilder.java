package dev.latvian.mods.kubejs.block.custom;

import dev.latvian.mods.kubejs.client.ModelGenerator;
import dev.latvian.mods.kubejs.client.MultipartBlockStateGenerator;
import dev.latvian.mods.kubejs.generator.KubeAssetGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WallBlock;

public class WallBlockBuilder extends ShapedBlockBuilder {
	public static final ResourceLocation[] WALL_TAGS = {
		BlockTags.WALLS.location(),
	};

	public WallBlockBuilder(ResourceLocation i) {
		super(i, "_wall");
		tagBoth(WALL_TAGS);
	}

	@Override
	public Block createObject() {
		return new WallBlock(createProperties());
	}

	@Override
	protected boolean useMultipartBlockState() {
		return true;
	}

	@Override
	protected void generateMultipartBlockState(MultipartBlockStateGenerator bs) {
		var modPost = newID("block/", "_post");
		var modSide = newID("block/", "_side");
		var modSideTall = newID("block/", "_side_tall");

		bs.part("up=true", modPost);
		bs.part("north=low", p -> p.model(modSide).uvlock());
		bs.part("east=low", p -> p.model(modSide).uvlock().y(90));
		bs.part("south=low", p -> p.model(modSide).uvlock().y(180));
		bs.part("west=low", p -> p.model(modSide).uvlock().y(270));
		bs.part("north=tall", p -> p.model(modSideTall).uvlock());
		bs.part("east=tall", p -> p.model(modSideTall).uvlock().y(90));
		bs.part("south=tall", p -> p.model(modSideTall).uvlock().y(180));
		bs.part("west=tall", p -> p.model(modSideTall).uvlock().y(270));
	}

	@Override
	protected void generateItemModel(ModelGenerator m) {
		m.parent("minecraft:block/wall_inventory");
		m.texture("wall", textures.get("texture"));
	}

	@Override
	protected void generateBlockModel(KubeAssetGenerator generator) {
		var texture = textures.get("texture");

		generator.blockModel(newID("", "_post"), m -> {
			m.parent("minecraft:block/template_wall_post");
			m.texture("wall", texture);
		});

		generator.blockModel(newID("", "_side"), m -> {
			m.parent("minecraft:block/template_wall_side");
			m.texture("wall", texture);
		});

		generator.blockModel(newID("", "_side_tall"), m -> {
			m.parent("minecraft:block/template_wall_side_tall");
			m.texture("wall", texture);
		});
	}
// FIXME: fix connection
}
