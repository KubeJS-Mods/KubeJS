package dev.latvian.mods.kubejs.recipe.viewer.server;

import dev.latvian.mods.kubejs.fluid.FluidWrapper;
import dev.latvian.mods.kubejs.recipe.viewer.GroupEntriesKubeEvent;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import dev.latvian.mods.rhino.Context;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class ServerGroupFluidEntriesKubeEvent implements GroupEntriesKubeEvent {
	private final List<FluidData.Group> list;

	public ServerGroupFluidEntriesKubeEvent(List<FluidData.Group> list) {
		this.list = list;
	}

	@Override
	public void group(Context cx, Object filter, ResourceLocation groupId, Component description) {
		list.add(new FluidData.Group(FluidWrapper.wrapIngredient(RegistryAccessContainer.of(cx), filter), groupId, description));
	}
}
