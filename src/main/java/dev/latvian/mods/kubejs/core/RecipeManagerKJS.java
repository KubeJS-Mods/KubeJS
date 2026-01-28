package dev.latvian.mods.kubejs.core;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.Map;

public interface RecipeManagerKJS extends ReloadableServerResourceHolderKJS {
	default void kjs$replaceRecipes(Map<Identifier, RecipeHolder<?>> byName) {
		throw new NoMixinException();
	}

	default Map<Identifier, RecipeHolder<?>> kjs$getRecipeIdMap() {
		throw new NoMixinException();
	}
}