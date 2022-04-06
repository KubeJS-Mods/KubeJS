package dev.latvian.mods.kubejs.item.custom;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;

public class HoeItemBuilder extends HandheldItemBuilder {
	public HoeItemBuilder(ResourceLocation i) {
		super(i, -2F, -1F);
	}

	@Override
	public Item createObject() {
		return new HoeItem(toolTier, (int) attackDamageBaseline, speedBaseline, createItemProperties());
	}
}
