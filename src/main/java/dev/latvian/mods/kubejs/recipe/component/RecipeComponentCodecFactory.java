package dev.latvian.mods.kubejs.recipe.component;

import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceKey;

@FunctionalInterface
public interface RecipeComponentCodecFactory<CT extends RecipeComponent<?>> {
	MapCodec<CT> create(ResourceKey<RecipeComponentType<?>> typeKey);
}
