package dev.latvian.mods.kubejs.recipe.viewer.server;

import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.kubejs.recipe.viewer.RemoveEntriesKubeEvent;
import dev.latvian.mods.kubejs.script.KubeJSContext;
import dev.latvian.mods.kubejs.util.MutableBoolean;
import dev.latvian.mods.rhino.Context;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public class ServerRemoveItemEntriesKubeEvent implements RemoveEntriesKubeEvent {
	private final List<Ingredient> removedEntries;
	private final List<Ingredient> directlyRemovedEntries;
	private final MutableBoolean removeAll;

	public ServerRemoveItemEntriesKubeEvent(List<Ingredient> removedEntries, List<Ingredient> directlyRemovedEntries, MutableBoolean removeAll) {
		this.removedEntries = removedEntries;
		this.directlyRemovedEntries = directlyRemovedEntries;
		this.removeAll = removeAll;
	}

	@Override
	public void remove(Context cx, Object filter) {
		removedEntries.add(IngredientJS.wrap(((KubeJSContext) cx).getRegistries(), filter));
	}

	@Override
	public void removeDirectly(Context cx, Object filter) {
		directlyRemovedEntries.add(IngredientJS.wrap(((KubeJSContext) cx).getRegistries(), filter));
	}

	@Override
	public void removeAll() {
		removeAll.value = true;
	}

	@Override
	public Object[] getAllEntryValues() {
		throw new IllegalStateException("Not available on server side!");
	}
}
