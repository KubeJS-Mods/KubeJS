package dev.latvian.mods.kubejs.recipe.viewer.server;

import dev.latvian.mods.kubejs.fluid.FluidWrapper;
import dev.latvian.mods.kubejs.recipe.viewer.RemoveEntriesKubeEvent;
import dev.latvian.mods.kubejs.script.KubeJSContext;
import dev.latvian.mods.rhino.Context;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;

import java.util.List;

public class ServerRemoveFluidEntriesKubeEvent implements RemoveEntriesKubeEvent {
	private final List<FluidIngredient> removedEntries;

	public ServerRemoveFluidEntriesKubeEvent(List<FluidIngredient> removedEntries) {
		this.removedEntries = removedEntries;
	}

	@Override
	public void remove(Context cx, Object filter) {
		removedEntries.add(FluidWrapper.wrapIngredient(((KubeJSContext) cx).getRegistries(), filter));
	}
}
