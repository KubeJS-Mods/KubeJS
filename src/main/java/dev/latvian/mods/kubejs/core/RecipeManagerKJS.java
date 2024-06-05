package dev.latvian.mods.kubejs.core;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.Map;

public interface RecipeManagerKJS extends ReloadableServerResourceHolderKJS {
	default void kjs$replaceRecipes(Map<ResourceLocation, RecipeHolder<?>> byName) {
		throw new NoMixinException();
	}

	default Map<ResourceLocation, RecipeHolder<?>> kjs$getRecipeIdMap() {
		throw new NoMixinException();
	}
}