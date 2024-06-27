package dev.latvian.mods.kubejs.recipe.viewer.server;

import dev.latvian.mods.kubejs.fluid.FluidWrapper;
import dev.latvian.mods.kubejs.recipe.viewer.AddInformationKubeEvent;
import dev.latvian.mods.kubejs.script.KubeJSContext;
import dev.latvian.mods.rhino.Context;
import net.minecraft.network.chat.Component;

import java.util.List;

public class ServerAddFluidInformationKubeEvent implements AddInformationKubeEvent {
	private final List<FluidData.Info> list;

	public ServerAddFluidInformationKubeEvent(List<FluidData.Info> list) {
		this.list = list;
	}

	@Override
	public void add(Context cx, Object filter, List<Component> info) {
		list.add(new FluidData.Info(FluidWrapper.wrapIngredient(((KubeJSContext) cx).getRegistries(), filter), info));
	}
}
