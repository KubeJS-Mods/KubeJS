package dev.latvian.mods.kubejs.block;

import dev.latvian.mods.kubejs.generator.AssetJsonGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;

/**
 * @author Prunoideae
 */
public class SeedItemBuilder extends BlockItemBuilder {

	public SeedItemBuilder(ResourceLocation i) {
		super(i);
	}

	@Override
	public String getTranslationKeyGroup() {
		return "item";
	}

	@Override
	public Item createObject() {
		return new ItemNameBlockItem(blockBuilder.get(), createItemProperties());
	}

	@Override
	public void generateAssetJsons(AssetJsonGenerator generator) {
		if (modelJson != null) {
			generator.json(AssetJsonGenerator.asItemModelLocation(id), modelJson);
			return;
		}

		generator.itemModel(id, m -> {
			if (!parentModel.isEmpty()) {
				m.parent(parentModel);
			} else {
				m.parent("minecraft:item/generated");
			}

			if (textureJson.size() == 0) {
				texture(newID("item/", "").toString());
			}
			m.textures(textureJson);
		});
	}
}
