package dev.latvian.kubejs.item.custom;

import dev.latvian.kubejs.item.ItemBuilder;
import net.minecraft.world.item.HoeItem;

public class HoeItemJS extends HoeItem {
	public HoeItemJS(ItemBuilder builder) {
		super(builder.toolTier, (int) builder.attackDamageBaseline, builder.attackSpeedBaseline, builder.createItemProperties());
	}
}
