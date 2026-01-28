package dev.latvian.mods.kubejs.recipe.viewer;

import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.rhino.Context;
import net.minecraft.resources.Identifier;

public interface RemoveCategoriesKubeEvent extends KubeEvent {
	void remove(Context cx, Identifier[] categories);
}
