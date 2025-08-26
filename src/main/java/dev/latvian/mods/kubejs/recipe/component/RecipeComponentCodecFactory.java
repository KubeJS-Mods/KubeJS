package dev.latvian.mods.kubejs.recipe.component;

import com.mojang.serialization.MapCodec;
import dev.latvian.mods.kubejs.recipe.RecipeTypeRegistryContext;

@FunctionalInterface
public interface RecipeComponentCodecFactory<CT extends RecipeComponent<?>> {
	MapCodec<CT> create(RecipeComponentType<?> type, RecipeTypeRegistryContext ctx);
}
