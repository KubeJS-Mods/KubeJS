package dev.latvian.mods.kubejs.recipe.viewer.server;

import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.kubejs.recipe.viewer.GroupEntriesKubeEvent;
import dev.latvian.mods.kubejs.script.KubeJSContext;
import dev.latvian.mods.rhino.Context;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class ServerGroupItemEntriesKubeEvent implements GroupEntriesKubeEvent {
	private final List<ItemData.Group> list;

	public ServerGroupItemEntriesKubeEvent(List<ItemData.Group> list) {
		this.list = list;
	}

	@Override
	public void group(Context cx, ResourceLocation groupId, Component description, Object filter) {
		list.add(new ItemData.Group(groupId, description, IngredientJS.wrap(((KubeJSContext) cx).getRegistries(), filter)));
	}
}
