package dev.latvian.mods.kubejs.recipe.viewer;

import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.rhino.Context;
import net.minecraft.network.chat.Component;

import java.util.List;

public interface AddInformationKubeEvent extends KubeEvent {
	RecipeViewerEntryType getType();

	void add(Context cx, Object filter, Component title, List<Component> description);

	default void add(Context cx, Object filter, Component title) {
		add(cx, filter, title, List.of());
	}
}
