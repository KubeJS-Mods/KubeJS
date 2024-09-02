package dev.latvian.mods.kubejs.block.custom;

import dev.latvian.mods.kubejs.client.MultipartBlockStateGenerator;
import dev.latvian.mods.kubejs.generator.KubeAssetGenerator;
import dev.latvian.mods.kubejs.util.ID;
import net.minecraft.resources.ResourceLocation;

public abstract class MultipartShapedBlockBuilder extends ShapedBlockBuilder {
	public MultipartShapedBlockBuilder(ResourceLocation i, String... suffixes) {
		super(i, suffixes);
	}

	@Override
	public void generateAssets(KubeAssetGenerator generator) {
		if (blockstateJson != null) {
			generator.json(id.withPath(ID.BLOCKSTATE), blockstateJson);
		} else {
			generator.multipartState(id, this::generateMultipartBlockStateJson);
		}

		if (modelJson != null) {
			generator.json(id.withPath(ID.MODEL), modelJson);
		} else {
			generateBlockModelJsons(generator);
		}

		if (itemBuilder != null) {
			if (itemBuilder.modelJson != null) {
				generator.json(id.withPath(ID.ITEM_MODEL), itemBuilder.modelJson);
			} else {
				generator.itemModel(itemBuilder.id, this::generateItemModelJson);
			}
		}
	}

	protected abstract void generateMultipartBlockStateJson(MultipartBlockStateGenerator bs);
}
