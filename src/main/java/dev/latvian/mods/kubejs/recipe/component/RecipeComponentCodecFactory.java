package dev.latvian.mods.kubejs.recipe.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchemaStorage;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import org.apache.commons.lang3.mutable.Mutable;
import org.jetbrains.annotations.ApiStatus;

@FunctionalInterface
public interface RecipeComponentCodecFactory<CT extends RecipeComponent<?>> {
	record Context(
		RegistryAccessContainer registries,
		RecipeSchemaStorage storage,
		@ApiStatus.Internal Mutable<Codec<RecipeComponent<?>>> unsetCodec
	) {
		public Codec<RecipeComponent<?>> codec() {
			return unsetCodec.getValue();
		}
	}

	MapCodec<CT> create(RecipeComponentType<?> type, Context ctx);
}
