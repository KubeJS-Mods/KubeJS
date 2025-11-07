package dev.latvian.mods.kubejs.item.creativetab;

import dev.latvian.mods.kubejs.item.ItemPredicate;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

import java.util.List;

public record CreativeTabCallbackForge(BuildCreativeModeTabContentsEvent event) implements CreativeTabCallback {
	@Override
	public void addAfter(ItemStack order, ItemStack[] items, CreativeModeTab.TabVisibility visibility) {
		for (var item : items) {
			if (order.isEmpty()) {
				event.accept(item, visibility);
			} else {
				event.insertAfter(order, item, visibility);
			}
		}
	}

	@Override
	public void addBefore(ItemStack order, ItemStack[] items, CreativeModeTab.TabVisibility visibility) {
		for (var item : items) {
			if (order.isEmpty()) {
				event.insertFirst(item, visibility);
			} else {
				event.insertBefore(order, item, visibility);
			}
		}
	}

	@Override
	public void remove(ItemPredicate filter, boolean removeParent, boolean removeSearch) {
		if (removeParent) {
			for (var is : List.copyOf(event.getParentEntries())) {
				if (filter.test(is)) {
					event.remove(is, CreativeModeTab.TabVisibility.PARENT_TAB_ONLY);
				}
			}
		}

		if (removeSearch) {
			for (var is : List.copyOf(event.getSearchEntries())) {
				if (filter.test(is)) {
					event.remove(is, CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY);
				}
			}
		}
	}
}