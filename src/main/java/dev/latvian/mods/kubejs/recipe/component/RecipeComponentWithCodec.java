package dev.latvian.mods.kubejs.recipe.component;

import com.mojang.serialization.Codec;

public record RecipeComponentWithCodec<T>(RecipeComponent<T> parent, Codec<T> codec) implements RecipeComponentWithParent<T> {
	@Override
	public RecipeComponentType<?> type() {
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
		return "custom_codec<" + parent + ">";
	}
}
