package dev.latvian.mods.kubejs.item.type;

import dev.latvian.mods.kubejs.item.ItemBuilder;
import dev.latvian.mods.kubejs.item.custom.BasicItemJS;
import net.minecraft.world.item.Item;

public class BasicItemType extends ItemType {
	public static final BasicItemType INSTANCE = new BasicItemType("basic");

	public BasicItemType(String n) {
		super(n);
	}

	@Override
	public Item createItem(ItemBuilder builder) {
		return new BasicItemJS(builder);
	}
}
