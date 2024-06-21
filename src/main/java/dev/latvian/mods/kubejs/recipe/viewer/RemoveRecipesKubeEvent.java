package dev.latvian.mods.kubejs.recipe.viewer;

import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.rhino.Context;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public interface RemoveRecipesKubeEvent extends KubeEvent {
	default void remove(Context cx, ResourceLocation[] recipesToRemove) {
		removeFromCategory(cx, null, recipesToRemove);
	}

	void removeFromCategory(Context cx, @Nullable ResourceLocation category, ResourceLocation[] recipesToRemove);
}
