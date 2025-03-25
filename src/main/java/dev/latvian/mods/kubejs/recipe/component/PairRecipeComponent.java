package dev.latvian.mods.kubejs.recipe.component;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.rhino.type.JSFixedArrayTypeInfo;
import dev.latvian.mods.rhino.type.JSOptionalParam;
import dev.latvian.mods.rhino.type.TypeInfo;

import java.util.List;

public record PairRecipeComponent<A, B>(RecipeComponent<A> a, RecipeComponent<B> b, Codec<Pair<A, B>> codec) implements RecipeComponent<Pair<A, B>> {
	public static final RecipeComponentType<Pair<?, ?>> TYPE = RecipeComponentType.dynamic(KubeJS.id("pair"), (RecipeComponentCodecFactory<PairRecipeComponent<?, ?>>) ctx -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		ctx.codec().fieldOf("a").forGetter(PairRecipeComponent::a),
		ctx.codec().fieldOf("b").forGetter(PairRecipeComponent::b)
	).apply(instance, PairRecipeComponent::new)));

	@Override
	public RecipeComponentType<?> type() {
		return TYPE;
	}

	public PairRecipeComponent(RecipeComponent<A> a, RecipeComponent<B> b) {
		this(a, b, Codec.pair(a.codec(), b.codec()));
	}

	@Override
	public Codec<Pair<A, B>> codec() {
		return codec;
	}

	@Override
	public TypeInfo typeInfo() {
		return new JSFixedArrayTypeInfo(List.of(new JSOptionalParam("", a.typeInfo()), new JSOptionalParam("", b.typeInfo())));
	}

	@Override
	public void buildUniqueId(UniqueIdBuilder builder, Pair<A, B> value) {
		a.buildUniqueId(builder, value.getFirst());
		builder.appendSeparator();
		b.buildUniqueId(builder, value.getSecond());
	}

	@Override
	public String toString() {
		return "pair<" + a + ", " + b + ">";
	}

	@Override
	public void validate(Pair<A, B> value) {
		a.validate(value.getFirst());
		b.validate(value.getSecond());
	}

	@Override
	public boolean isEmpty(Pair<A, B> value) {
		return a.isEmpty(value.getFirst()) && b.isEmpty(value.getSecond());
	}
}
