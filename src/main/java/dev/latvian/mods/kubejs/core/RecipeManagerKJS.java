package dev.latvian.mods.kubejs.core;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;

import java.util.Collection;
import java.util.Map;

public interface RecipeManagerKJS extends ReloadableServerResourceHolderKJS {
	default void kjs$replaceRecipes(RecipeMap recipeMap) {
		throw new NoMixinException();
	}

	default Collection<RecipeHolder<?>> kjs$getRecipes() {
		throw new NoMixinException();
	}
}