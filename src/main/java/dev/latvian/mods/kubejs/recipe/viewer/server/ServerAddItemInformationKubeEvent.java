package dev.latvian.mods.kubejs.recipe.viewer.server;

import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.kubejs.recipe.viewer.AddInformationKubeEvent;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import dev.latvian.mods.rhino.Context;
import net.minecraft.network.chat.Component;

import java.util.List;

public class ServerAddItemInformationKubeEvent implements AddInformationKubeEvent {
	private final List<ItemData.Info> list;

	public ServerAddItemInformationKubeEvent(List<ItemData.Info> list) {
		this.list = list;
	}

	@Override
	public void add(Context cx, Object filter, List<Component> info) {
		list.add(new ItemData.Info(IngredientJS.wrap(RegistryAccessContainer.of(cx), filter), info));
	}
}
