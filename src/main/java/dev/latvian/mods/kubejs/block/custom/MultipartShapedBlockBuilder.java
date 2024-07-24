package dev.latvian.mods.kubejs.block.custom;

import dev.latvian.mods.kubejs.client.MultipartBlockStateGenerator;
import dev.latvian.mods.kubejs.generator.KubeAssetGenerator;
import net.minecraft.resources.ResourceLocation;

public abstract class MultipartShapedBlockBuilder extends ShapedBlockBuilder {
	public MultipartShapedBlockBuilder(ResourceLocation i, String... suffixes) {
		super(i, suffixes);
	}

	@Override
	public void generateAssets(KubeAssetGenerator generator) {
		if (blockstateJson != null) {
			generator.json(newID("blockstates/", ""), blockstateJson);
		} else {
			generator.multipartState(id, this::generateMultipartBlockStateJson);
		}

		if (modelJson != null) {
			generator.json(newID("models/", ""), modelJson);
		} else {
			generateBlockModelJsons(generator);
		}

		if (itemBuilder != null) {
			if (itemBuilder.modelJson != null) {
				generator.json(newID("models/item/", ""), itemBuilder.modelJson);
			} else {
				generator.itemModel(itemBuilder.id, this::generateItemModelJson);
			}
		}


	}

	protected abstract void generateMultipartBlockStateJson(MultipartBlockStateGenerator bs);
}
