package dev.latvian.mods.kubejs.recipe.component;

import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceKey;

public interface RecipeComponentTypeRegistry {
	void register(ResourceKey<RecipeComponentType<?>> key, RecipeComponentType<?> type);

	@SuppressWarnings("unchecked")
	default <CT extends RecipeComponent<?>> void register(ResourceKey<RecipeComponentType<?>> key, MapCodec<CT> mapCodec) {
		register(key, () -> (MapCodec<RecipeComponent<?>>) mapCodec);
	}

	default <CT extends RecipeComponent<?>> void unit(ResourceKey<RecipeComponentType<?>> key, CT instance) {
		register(key, () -> MapCodec.unit(instance));
	}

	default <CT extends RecipeComponent<?>> void unit(CT instance) {
		register(instance.type(), () -> MapCodec.unit(instance));
	}

	@SuppressWarnings("unchecked")
	default <CT extends RecipeComponent<?>> void fromFactory(ResourceKey<RecipeComponentType<?>> key, RecipeComponentCodecFactory<CT> codecFactory) {
		register(key, () -> (MapCodec<RecipeComponent<?>>) codecFactory.create(key));
	}
}
