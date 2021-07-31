package dev.latvian.kubejs.item.custom;

import dev.latvian.kubejs.item.ItemBuilder;
import net.minecraft.world.item.Item;

public abstract class ItemType {
	public final String name;

	public ItemType(String n) {
		name = n;
	}

	public abstract Item createItem(ItemBuilder builder);

	public void applyDefaults(ItemBuilder builder) {
	}
}
