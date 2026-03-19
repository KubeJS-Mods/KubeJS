package dev.latvian.mods.kubejs.recipe.viewer;

import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.rhino.Context;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public interface RemoveRecipesKubeEvent extends KubeEvent {
	default void remove(Context cx, Identifier[] recipesToRemove) {
		removeFromCategory(cx, null, recipesToRemove);
	}

	void removeFromCategory(Context cx, @Nullable Identifier category, Identifier[] recipesToRemove);
}
