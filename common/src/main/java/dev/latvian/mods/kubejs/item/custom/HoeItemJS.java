package dev.latvian.mods.kubejs.item.custom;

import dev.latvian.mods.kubejs.item.ItemBuilder;
import net.minecraft.world.item.HoeItem;

public class HoeItemJS extends HoeItem {
	public HoeItemJS(ItemBuilder builder) {
		super(builder.toolTier, (int) builder.attackDamageBaseline, builder.attackSpeedBaseline, builder.createItemProperties());
	}
}
