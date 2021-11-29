package dev.latvian.mods.kubejs.item.custom;

import dev.latvian.mods.kubejs.generator.AssetJsonGenerator;
import dev.latvian.mods.kubejs.generator.DataJsonGenerator;
import dev.latvian.mods.kubejs.item.ItemBuilder;
import net.minecraft.world.item.Item;

public abstract class ItemType {
	public final String name;

	public ItemType(String n) {
		name = n;
	}

	public abstract Item createItem(ItemBuilder builder);

	public void applyDefaults(ItemBuilder builder) {
	}

	public void generateAssets(ItemBuilder builder, AssetJsonGenerator generator) {
		generator.itemModel(builder.id, m -> {
			if (!builder.parentModel.isEmpty()) {
				m.parent(builder.parentModel);
			} else {
				m.parent("minecraft:item/generated");
			}

			m.texture("layer0", builder.texture.isEmpty() ? builder.newID("item/", "").toString() : builder.texture);
		});
	}

	public void generateData(ItemBuilder builder, DataJsonGenerator generator) {
	}
}
