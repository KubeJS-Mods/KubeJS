package dev.latvian.mods.kubejs.recipe.component;

import com.mojang.serialization.Codec;
import dev.latvian.mods.rhino.type.TypeInfo;

public class SimpleRecipeComponent<T> implements RecipeComponent<T> {
	public final RecipeComponentType<?> type;
	public final Codec<T> codec;
	public final TypeInfo typeInfo;

	public SimpleRecipeComponent(RecipeComponentType<?> type, Codec<T> codec, TypeInfo typeInfo) {
		this.type = type;
		this.codec = codec;
		this.typeInfo = typeInfo;
	}

	@Override
	public RecipeComponentType<?> type() {
		return type;
	}

	@Override
	public Codec<T> codec() {
		return codec;
	}

	@Override
	public TypeInfo typeInfo() {
		return typeInfo;
	}

	@Override
	public String toString() {
		return type.toString();
	}
}
