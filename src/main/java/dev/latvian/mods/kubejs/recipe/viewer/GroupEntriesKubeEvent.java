package dev.latvian.mods.kubejs.recipe.viewer;

import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.rhino.Context;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public interface GroupEntriesKubeEvent extends KubeEvent {
	void group(Context cx, Object filter, ResourceLocation groupId, Component description);
}
