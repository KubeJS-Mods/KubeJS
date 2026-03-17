package dev.latvian.mods.kubejs.recipe.component;

import com.mojang.serialization.MapCodec;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.registry.KubeJSRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.ApiStatus;

@FunctionalInterface
public interface RecipeComponentType<T> {
	static ResourceKey<RecipeComponentType<?>> key(Identifier id) {
		return ResourceKey.create(KubeJSRegistries.RECIPE_COMPONENT_TYPE, id);
	}

	@ApiStatus.Internal
	static ResourceKey<RecipeComponentType<?>> builtin(String name) {
		return key(KubeJS.id(name));
	}

	MapCodec<RecipeComponent<?>> mapCodec();
}
