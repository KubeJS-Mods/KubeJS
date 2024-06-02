package dev.latvian.mods.kubejs.recipe.component;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import dev.latvian.mods.rhino.type.JSFixedArrayTypeInfo;
import dev.latvian.mods.rhino.type.TypeInfo;

public record AndRecipeComponent<A, B>(RecipeComponent<A> a, RecipeComponent<B> b, Codec<Pair<A, B>> codec) implements RecipeComponent<Pair<A, B>> {
	public AndRecipeComponent(RecipeComponent<A> a, RecipeComponent<B> b) {
		this(a, b, Codec.pair(a.codec(), b.codec()));
	}

	@Override
	public Codec<Pair<A, B>> codec() {
		return codec;
	}

	@Override
	public TypeInfo typeInfo() {
		return new JSFixedArrayTypeInfo(a.typeInfo(), b.typeInfo());
	}

	@Override
	public String toString() {
		return "pair<" + a + ", " + b + ">";
	}
}
