package dev.latvian.mods.kubejs.recipe.viewer.server;

import dev.latvian.mods.kubejs.fluid.FluidWrapper;
import dev.latvian.mods.kubejs.recipe.viewer.GroupEntriesKubeEvent;
import dev.latvian.mods.rhino.Context;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.List;

public class ServerGroupFluidEntriesKubeEvent implements GroupEntriesKubeEvent {
	private final List<FluidData.Group> list;

	public ServerGroupFluidEntriesKubeEvent(List<FluidData.Group> list) {
		this.list = list;
	}

	@Override
	public void group(Context cx, Object filter, Identifier groupId, Component description) {
		list.add(new FluidData.Group(FluidWrapper.wrapIngredient(cx, filter), groupId, description));
	}
}
