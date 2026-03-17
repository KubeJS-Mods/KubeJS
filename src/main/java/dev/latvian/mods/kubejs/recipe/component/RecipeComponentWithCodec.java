package dev.latvian.mods.kubejs.recipe.component;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceKey;

public record RecipeComponentWithCodec<T>(RecipeComponent<T> parent, Codec<T> codec) implements RecipeComponentWithParent<T> {
	@Override
	public ResourceKey<RecipeComponentType<?>> type() {
		return parent.type();
	}

	@Override
	public RecipeComponent<T> parentComponent() {
		return parent;
	}

	@Override
	public Codec<T> codec() {
		return codec;
	}

	@Override
	public String toString() {
		return parent + "{custom_codec}";
	}
}
