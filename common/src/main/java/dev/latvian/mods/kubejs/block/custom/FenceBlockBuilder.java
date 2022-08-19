package dev.latvian.mods.kubejs.block.custom;

import dev.architectury.platform.Platform;
import dev.latvian.mods.kubejs.client.ModelGenerator;
import dev.latvian.mods.kubejs.client.MultipartBlockStateGenerator;
import dev.latvian.mods.kubejs.generator.AssetJsonGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceBlock;

public class FenceBlockBuilder extends MultipartShapedBlockBuilder {
	public FenceBlockBuilder(ResourceLocation i) {
		super(i, "_fence");

		tagBoth(BlockTags.FENCES.location());

		if (Platform.isForge()) {
			tagBoth(new ResourceLocation("forge:fences"));
		}
	}

	@Override
	public Block createObject() {
		return new FenceBlock(createProperties());
	}

	@Override
	protected void generateMultipartBlockstateJson(MultipartBlockStateGenerator bs) {
		var modPost = newID("block/", "_post").toString();
		var modSide = newID("block/", "_side").toString();

		bs.part("", modPost);
		bs.part("north=true", p -> p.model(modSide).uvlock());
		bs.part("east=true", p -> p.model(modSide).uvlock().y(90));
		bs.part("south=true", p -> p.model(modSide).uvlock().y(180));
		bs.part("west=true", p -> p.model(modSide).uvlock().y(270));
	}

	@Override
	protected void generateItemModelJson(ModelGenerator m) {
		m.parent("minecraft:block/fence_inventory");
		m.texture("texture", textures.get("texture").getAsString());
	}

	@Override
	protected void generateBlockModelJsons(AssetJsonGenerator generator) {
		var texture = textures.get("texture").getAsString();

		generator.blockModel(newID("", "_post"), m -> {
			m.parent("minecraft:block/fence_post");
			m.texture("texture", texture);
		});
		generator.blockModel(newID("", "_side"), m -> {
			m.parent("minecraft:block/fence_side");
			m.texture("texture", texture);
		});
	}
}
