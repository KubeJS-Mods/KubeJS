package dev.latvian.kubejs.item.custom;

import dev.latvian.kubejs.item.ItemBuilder;
import net.minecraft.world.item.SwordItem;

public class SwordItemJS extends SwordItem {
	public SwordItemJS(ItemBuilder builder) {
		super(builder.toolTier, (int) builder.attackDamageBaseline, builder.attackSpeedBaseline, builder.createItemProperties());
	}
}
