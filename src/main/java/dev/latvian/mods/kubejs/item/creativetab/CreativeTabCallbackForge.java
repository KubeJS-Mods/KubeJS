package dev.latvian.mods.kubejs.item.creativetab;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;

public record CreativeTabCallbackForge(BuildCreativeModeTabContentsEvent event) implements CreativeTabCallback {
	@Override
	public void addAfter(ItemStack order, ItemStack[] items, CreativeModeTab.TabVisibility visibility) {
		for (var item : items) {
			event.accept(item, visibility);
		}
	}

	@Override
	public void addBefore(ItemStack order, ItemStack[] items, CreativeModeTab.TabVisibility visibility) {
		for (var item : items) {
			event.accept(item, visibility);
		}
	}

	@Override
	public void remove(Ingredient filter, boolean removeDisplay, boolean removeSearch) {
		var entries = new ArrayList<Map.Entry<ItemStack, CreativeModeTab.TabVisibility>>();

		for (var entry : event.getEntries()) {
			if (filter.test(entry.getKey())) {
				var visibility = entry.getValue();

				if (removeDisplay && removeSearch) {
					visibility = null;
				}

				if (removeDisplay && visibility == CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS) {
					visibility = CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY;
				}

				if (removeSearch && visibility == CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS) {
					visibility = CreativeModeTab.TabVisibility.PARENT_TAB_ONLY;
				}

				entries.add(new AbstractMap.SimpleEntry<>(entry.getKey(), visibility));
			}
		}

		for (var entry : entries) {
			if (entry.getValue() == null) {
				event.getEntries().remove(entry.getKey());
			} else {
				event.getEntries().put(entry.getKey(), entry.getValue());
			}
		}
	}
}