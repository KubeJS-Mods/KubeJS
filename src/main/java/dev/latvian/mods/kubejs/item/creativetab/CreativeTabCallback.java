package dev.latvian.mods.kubejs.item.creativetab;

import dev.latvian.mods.kubejs.item.ItemPredicate;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public interface CreativeTabCallback {
	void addAfter(ItemStack order, ItemStack[] items, CreativeModeTab.TabVisibility visibility);

	void addBefore(ItemStack order, ItemStack[] items, CreativeModeTab.TabVisibility visibility);

	void remove(ItemPredicate filter, boolean removeDisplay, boolean removeSearch);
}