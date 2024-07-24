package dev.latvian.mods.kubejs.fluid;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.generator.KubeAssetGenerator;
import dev.latvian.mods.kubejs.item.ItemBuilder;
import net.minecraft.world.item.BucketItem;

public class FluidBucketItemBuilder extends ItemBuilder {
	public final FluidBuilder fluidBuilder;

	public FluidBucketItemBuilder(FluidBuilder b) {
		super(b.newID("", "_bucket"));
		fluidBuilder = b;
		maxStackSize(1);
	}

	@Override
	public BucketItem createObject() {
		return new BucketItem(fluidBuilder.get(), createItemProperties());
	}

	@Override
	public void generateAssets(KubeAssetGenerator generator) {
		if (modelJson != null) {
			generator.json(KubeAssetGenerator.asItemModelLocation(id), modelJson);
			return;
		}

		boolean maskTexture = fluidBuilder.bucketTint == null && generator.mask(newID("item/", "_bucket_fluid"), KubeJS.id("item/bucket_mask"), fluidBuilder.fluidType.stillTexture);

		generator.itemModel(id, m -> {
			if (!parentModel.isEmpty()) {
				m.parent(parentModel);
			} else {
				m.parent("kubejs:item/generated_bucket");
			}

			if (maskTexture) {
				m.texture("bucket_fluid", newID("item/", "_bucket_fluid"));
			} else {
				m.texture("bucket_fluid", "kubejs:item/bucket_fluid");
			}

			if (!textureJson.isEmpty()) {
				m.textures(textureJson);
			}
		});
	}
}
