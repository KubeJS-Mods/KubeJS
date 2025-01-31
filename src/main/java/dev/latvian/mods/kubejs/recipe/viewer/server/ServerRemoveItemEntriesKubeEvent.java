package dev.latvian.mods.kubejs.recipe.viewer.server;

import dev.latvian.mods.kubejs.plugin.builtin.wrapper.IngredientWrapper;
import dev.latvian.mods.kubejs.recipe.viewer.RemoveEntriesKubeEvent;
import dev.latvian.mods.rhino.Context;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public class ServerRemoveItemEntriesKubeEvent implements RemoveEntriesKubeEvent {
	private final List<Ingredient> removedEntries;

	public ServerRemoveItemEntriesKubeEvent(List<Ingredient> removedEntries) {
		this.removedEntries = removedEntries;
	}

	@Override
	public void remove(Context cx, Object filter) {
		removedEntries.add(IngredientWrapper.wrap(cx, filter));
	}
}
