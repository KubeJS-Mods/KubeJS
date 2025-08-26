package dev.latvian.mods.kubejs.recipe.component;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import dev.latvian.mods.kubejs.util.OpsContainer;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;

import java.util.List;

@SuppressWarnings("OptionalIsPresent")
public record EitherRecipeComponent<H, L>(RecipeComponent<H> left, RecipeComponent<L> right, Codec<Either<H, L>> codec, TypeInfo typeInfo) implements RecipeComponent<Either<H, L>> {
	public static final RecipeComponentType<?> TYPE = RecipeComponentType.<EitherRecipeComponent<?, ?>>dynamic(KubeJS.id("either"), (type, ctx) -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		ctx.recipeComponentCodec().fieldOf("left").forGetter(EitherRecipeComponent::left),
		ctx.recipeComponentCodec().fieldOf("right").forGetter(EitherRecipeComponent::right)
	).apply(instance, EitherRecipeComponent::new)));

	public EitherRecipeComponent(RecipeComponent<H> high, RecipeComponent<L> low) {
		this(high, low, Codec.either(high.codec(), low.codec()), high.typeInfo().or(low.typeInfo()));
	}

	@Override
	public RecipeComponentType<?> type() {
		return TYPE;
	}

	@Override
	public Either<H, L> wrap(Context cx, KubeRecipe recipe, Object from) {
		if (left.hasPriority(cx, recipe, from)) {
			// if high has priority, only try to read high
			return Either.left(left.wrap(cx, recipe, from));
		} else if (right.hasPriority(cx, recipe, from)) {
			// if low has priority, only try to read low
			return Either.right(right.wrap(cx, recipe, from));
		} else {
			// If neither has priority, try to read high, if it fails, fallback to low
			try {
				return Either.left(left.wrap(cx, recipe, from));
			} catch (Exception ex1) {
				try {
					return Either.right(right.wrap(cx, recipe, from));
				} catch (Exception ex2) {
					ConsoleJS.SERVER.warn("Failed to read %s (left: %s)!".formatted(from, left), ex1);
					ConsoleJS.SERVER.warn("Failed to read %s (right: %s)!".formatted(from, right), ex2);
					throw new KubeRuntimeException("Failed to read %s as either %s or %s!".formatted(from, left, right)).source(recipe.sourceLine);
				}
			}
		}
	}

	@Override
	public boolean matches(Context cx, KubeRecipe recipe, Either<H, L> value, ReplacementMatchInfo match) {
		var l = value.left();
		return l.isPresent() ? left.matches(cx, recipe, l.get(), match) : right.matches(cx, recipe, value.right().get(), match);
	}

	@Override
	public Either<H, L> replace(Context cx, KubeRecipe recipe, Either<H, L> original, ReplacementMatchInfo match, Object with) {
		var l = original.left();

		if (l.isPresent()) {
			var r = left.replace(cx, recipe, l.get(), match, with);
			return r == l.get() ? original : Either.left(r);
		} else {
			var r = right.replace(cx, recipe, original.right().get(), match, with);
			return r == original.right().get() ? original : Either.right(r);
		}
	}

	@Override
	public void buildUniqueId(UniqueIdBuilder builder, Either<H, L> value) {
		var l = value.left();

		if (l.isPresent()) {
			left.buildUniqueId(builder, l.get());
		} else {
			right.buildUniqueId(builder, value.right().get());
		}
	}

	@Override
	public void validate(ValidationContext ctx, Either<H, L> value) {
		ctx.stack().push(this);

		var l = value.left();

		if (l.isPresent()) {
			ctx.stack().setKey("left");
			left.validate(ctx, l.get());
		} else {
			ctx.stack().setKey("right");
			right.validate(ctx, value.right().get());
		}

		ctx.stack().pop();
	}

	@Override
	public String toString() {
		return "either<" + left + ", " + right + ">";
	}

	@Override
	public String toString(OpsContainer ops, Either<H, L> value) {
		var l = value.left();

		if (l.isPresent()) {
			return left.toString(ops, l.get());
		} else {
			return right.toString(ops, value.right().get());
		}
	}

	@Override
	public List<?> spread(Either<H, L> value) {
		var l = value.left();

		if (l.isPresent()) {
			return left.spread(l.get());
		} else {
			return right.spread(value.right().get());
		}
	}
}
