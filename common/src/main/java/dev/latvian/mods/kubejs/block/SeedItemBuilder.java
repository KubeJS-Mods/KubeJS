package dev.latvian.mods.kubejs.block;

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
}
