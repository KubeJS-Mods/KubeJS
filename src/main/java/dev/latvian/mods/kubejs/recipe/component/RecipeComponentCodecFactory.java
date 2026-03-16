package dev.latvian.mods.kubejs.recipe.component;

import com.mojang.serialization.MapCodec;

@FunctionalInterface
public interface RecipeComponentCodecFactory<CT extends RecipeComponent<?>> {
	MapCodec<CT> create(RecipeComponentType<?> type);
}
