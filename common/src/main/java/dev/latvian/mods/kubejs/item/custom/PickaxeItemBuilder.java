package dev.latvian.mods.kubejs.item.custom;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PickaxeItem;

public class PickaxeItemBuilder extends HandheldItemBuilder {
	public PickaxeItemBuilder(ResourceLocation i) {
		super(i, 1F, -2.8F);
	}

	@Override
	public Item createObject() {
		return new PickaxeItem(toolTier, (int) attackDamageBaseline, speedBaseline, createItemProperties());
	}
}
