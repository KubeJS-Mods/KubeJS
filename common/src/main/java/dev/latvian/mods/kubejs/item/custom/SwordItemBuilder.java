package dev.latvian.mods.kubejs.item.custom;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;

public class SwordItemBuilder extends HandheldItemBuilder {
	public SwordItemBuilder(ResourceLocation i) {
		super(i, 3F, -2.4F);
	}

	@Override
	public Item createObject() {
		return new SwordItem(toolTier, (int) attackDamageBaseline, attackSpeedBaseline, createItemProperties());
	}
}
