package dev.latvian.mods.kubejs.recipe.viewer.server;

import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.kubejs.recipe.viewer.RemoveEntriesKubeEvent;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
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
		removedEntries.add(IngredientJS.wrap(RegistryAccessContainer.of(cx), filter));
	}
}
