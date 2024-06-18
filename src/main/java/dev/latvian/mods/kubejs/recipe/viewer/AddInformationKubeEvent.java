package dev.latvian.mods.kubejs.recipe.viewer;

import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.rhino.Context;
import net.minecraft.network.chat.Component;

public interface AddInformationKubeEvent extends KubeEvent {
	void add(Context cx, Object filter, Component[] info);
}
