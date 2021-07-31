package dev.latvian.kubejs.item.custom;

import dev.latvian.kubejs.item.ItemBuilder;
import dev.latvian.kubejs.item.ItemJS;
import net.minecraft.world.item.Item;

public class BasicItemType extends ItemType {
	public static final BasicItemType INSTANCE = new BasicItemType("basic");

	public BasicItemType(String n) {
		super(n);
	}

	@Override
	public Item createItem(ItemBuilder builder) {
		return new ItemJS(builder);
	}
}
