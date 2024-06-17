package dev.latvian.mods.kubejs.recipe.viewer;

import dev.latvian.mods.kubejs.event.KubeEvent;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;

public interface RemoveRecipesKubeEvent extends KubeEvent {
	void remove(ResourceLocation[] recipesToRemove);

	void removeFromCategory(ResourceLocation category, ResourceLocation[] recipesToRemove);

	Collection<ResourceLocation> getCategories();
}
