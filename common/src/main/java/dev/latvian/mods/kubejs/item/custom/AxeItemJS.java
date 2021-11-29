package dev.latvian.mods.kubejs.item.custom;

import dev.latvian.mods.kubejs.item.ItemBuilder;
import net.minecraft.world.item.AxeItem;

public class AxeItemJS extends AxeItem {
	public AxeItemJS(ItemBuilder builder) {
		super(builder.toolTier, builder.attackDamageBaseline, builder.attackSpeedBaseline, builder.createItemProperties());
	}
}
