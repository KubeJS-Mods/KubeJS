package dev.latvian.mods.kubejs.item.creativetab;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public interface CreativeTabCallback {
	void addAfter(ItemStack order, ItemStack[] items, CreativeModeTab.TabVisibility visibility);

	void addBefore(ItemStack order, ItemStack[] items, CreativeModeTab.TabVisibility visibility);

	void remove(Ingredient filter, boolean removeDisplay, boolean removeSearch);
}