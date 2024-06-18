package dev.latvian.mods.kubejs.recipe.viewer;

import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.rhino.Context;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;

public interface RemoveCategoriesKubeEvent extends KubeEvent {
	void remove(Context cx, ResourceLocation[] categories);

	Collection<ResourceLocation> getCategories();
}
