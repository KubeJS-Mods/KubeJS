package dev.latvian.mods.kubejs.recipe.viewer.server;

import dev.latvian.mods.kubejs.fluid.FluidWrapper;
import dev.latvian.mods.kubejs.recipe.viewer.RegisterSubtypesKubeEvent;
import dev.latvian.mods.kubejs.recipe.viewer.SubtypeInterpreter;
import dev.latvian.mods.kubejs.script.KubeJSContext;
import dev.latvian.mods.rhino.Context;
import net.minecraft.core.component.DataComponentType;

import java.util.List;

public class ServerRegisterFluidSubtypesKubeEvent implements RegisterSubtypesKubeEvent {
	private final List<FluidData.DataComponentSubtypes> list;

	public ServerRegisterFluidSubtypesKubeEvent(List<FluidData.DataComponentSubtypes> list) {
		this.list = list;
	}

	@Override
	public void register(Context cx, Object filter, SubtypeInterpreter interpreter) {
		throw new UnsupportedOperationException("Not available on server side!");
	}

	@Override
	public void useComponents(Context cx, Object filter, List<DataComponentType<?>> components) {
		list.add(new FluidData.DataComponentSubtypes(FluidWrapper.wrapIngredient(((KubeJSContext) cx).getRegistries(), filter), components));
	}
}
