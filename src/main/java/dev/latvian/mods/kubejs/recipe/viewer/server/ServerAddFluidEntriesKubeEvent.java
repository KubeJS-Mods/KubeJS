package dev.latvian.mods.kubejs.recipe.viewer.server;

import dev.latvian.mods.kubejs.fluid.FluidWrapper;
import dev.latvian.mods.kubejs.recipe.viewer.AddEntriesKubeEvent;
import dev.latvian.mods.rhino.Context;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

public class ServerAddFluidEntriesKubeEvent implements AddEntriesKubeEvent {
	private final List<FluidStack> list;

	public ServerAddFluidEntriesKubeEvent(List<FluidStack> list) {
		this.list = list;
	}

	@Override
	public void add(Context cx, Object[] items) {
		for (var item : items) {
			list.add(FluidWrapper.wrap(cx, item));
		}
	}
}
