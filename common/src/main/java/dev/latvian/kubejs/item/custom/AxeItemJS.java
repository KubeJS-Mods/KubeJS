package dev.latvian.kubejs.item.custom;

import dev.latvian.kubejs.item.ItemBuilder;
import net.minecraft.world.item.AxeItem;

public class AxeItemJS extends AxeItem {
	public AxeItemJS(ItemBuilder builder) {
		super(builder.toolTier, builder.attackDamageBaseline, builder.attackSpeedBaseline, builder.createItemProperties());
	}
}
