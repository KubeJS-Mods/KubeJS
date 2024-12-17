package dev.latvian.mods.kubejs.recipe.viewer.server;

import dev.latvian.mods.kubejs.bindings.IngredientWrapper;
import dev.latvian.mods.kubejs.recipe.viewer.RegisterSubtypesKubeEvent;
import dev.latvian.mods.kubejs.recipe.viewer.SubtypeInterpreter;
import dev.latvian.mods.rhino.Context;
import net.minecraft.core.component.DataComponentType;

import java.util.List;

public class ServerRegisterItemSubtypesKubeEvent implements RegisterSubtypesKubeEvent {
	private final List<ItemData.DataComponentSubtypes> list;

	public ServerRegisterItemSubtypesKubeEvent(List<ItemData.DataComponentSubtypes> list) {
		this.list = list;
	}

	@Override
	public void register(Context cx, Object filter, SubtypeInterpreter interpreter) {
		throw new UnsupportedOperationException("Not available on server side!");
	}

	@Override
	public void useComponents(Context cx, Object filter, List<DataComponentType<?>> components) {
		list.add(new ItemData.DataComponentSubtypes(IngredientWrapper.wrap(cx, filter), components));
	}
}
