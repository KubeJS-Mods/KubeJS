package dev.latvian.mods.kubejs.recipe.viewer.server;

import dev.latvian.mods.kubejs.bindings.ItemWrapper;
import dev.latvian.mods.kubejs.recipe.viewer.AddEntriesKubeEvent;
import dev.latvian.mods.rhino.Context;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ServerAddItemEntriesKubeEvent implements AddEntriesKubeEvent {
	private final List<ItemStack> list;

	public ServerAddItemEntriesKubeEvent(List<ItemStack> list) {
		this.list = list;
	}

	@Override
	public void add(Context cx, Object[] items) {
		for (var item : items) {
			list.add(ItemWrapper.wrap(cx, item));
		}
	}
}
