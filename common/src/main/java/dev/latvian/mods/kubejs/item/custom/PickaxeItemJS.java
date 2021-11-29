package dev.latvian.mods.kubejs.item.custom;

import dev.latvian.mods.kubejs.item.ItemBuilder;
import net.minecraft.world.item.PickaxeItem;

public class PickaxeItemJS extends PickaxeItem {
	public PickaxeItemJS(ItemBuilder builder) {
		super(builder.toolTier, (int) builder.attackDamageBaseline, builder.attackSpeedBaseline, builder.createItemProperties());
	}
}
