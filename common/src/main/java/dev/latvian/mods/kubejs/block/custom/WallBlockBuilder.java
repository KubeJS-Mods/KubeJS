package dev.latvian.mods.kubejs.block.custom;

import dev.latvian.mods.kubejs.generator.AssetJsonGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WallBlock;

public class WallBlockBuilder extends ShapedBlockBuilder {
	public WallBlockBuilder(ResourceLocation i) {
		super(i, "_wall");
		tagBoth(BlockTags.WALLS.location());
	}

	@Override
	public Block createObject() {
		return new WallBlock(createProperties());
	}

	@Override
	public void generateAssetJsons(AssetJsonGenerator generator) {
		generator.multipartState(id, bs -> {
			var modPost = newID("block/", "_post").toString();
			var modSide = newID("block/", "_side").toString();
			var modSideTall = newID("block/", "_side_tall").toString();

			bs.part("up=true", modPost);
			bs.part("north=low", p -> p.model(modSide).uvlock());
			bs.part("east=low", p -> p.model(modSide).uvlock().y(90));
			bs.part("south=low", p -> p.model(modSide).uvlock().y(180));
			bs.part("west=low", p -> p.model(modSide).uvlock().y(270));
			bs.part("north=tall", p -> p.model(modSideTall).uvlock());
			bs.part("east=tall", p -> p.model(modSideTall).uvlock().y(90));
			bs.part("south=tall", p -> p.model(modSideTall).uvlock().y(180));
			bs.part("west=tall", p -> p.model(modSideTall).uvlock().y(270));
		});

		final var texture = textures.get("texture").getAsString();

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

		generator.itemModel(itemBuilder.id, m -> {
			m.parent("minecraft:block/wall_inventory");
			m.texture("wall", texture);
		});
	}

	// FIXME: fix connection
}
