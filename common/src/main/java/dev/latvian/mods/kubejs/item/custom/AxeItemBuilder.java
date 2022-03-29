package dev.latvian.mods.kubejs.item.custom;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;

public class AxeItemBuilder extends HandheldItemBuilder {
	public AxeItemBuilder(ResourceLocation i) {
		super(i, 6F, -3.1F);
	}

	@Override
	public Item createObject() {
		return new AxeItem(toolTier, attackDamageBaseline, attackSpeedBaseline, createItemProperties());
	}
}
