package dev.latvian.mods.kubejs.item.custom;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ShovelItem;

public class ShovelItemBuilder extends HandheldItemBuilder {
	public ShovelItemBuilder(ResourceLocation i) {
		super(i, 1.5F, -3F);
	}

	@Override
	public Item createObject() {
		return new ShovelItem(toolTier, attackDamageBaseline, attackSpeedBaseline, createItemProperties());
	}
}
