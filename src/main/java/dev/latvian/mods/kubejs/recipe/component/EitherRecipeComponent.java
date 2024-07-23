package dev.latvian.mods.kubejs.recipe.component;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import dev.latvian.mods.kubejs.recipe.schema.RecipeComponentFactory;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;

@SuppressWarnings("OptionalIsPresent")
public record EitherRecipeComponent<H, L>(RecipeComponent<H> high, RecipeComponent<L> low) implements RecipeComponent<Either<H, L>> {
	public static final RecipeComponentFactory FACTORY = RecipeComponentFactory.readTwoComponents(EitherRecipeComponent::new);

	@Override
	public Codec<Either<H, L>> codec() {
		return Codec.either(high.codec(), low.codec());
	}

	@Override
	public TypeInfo typeInfo() {
		return high.typeInfo().or(low.typeInfo());
	}

	@Override
	public Either<H, L> wrap(Context cx, KubeRecipe recipe, Object from) {
		if (high.hasPriority(cx, recipe, from)) {
			// if high has priority, only try to read high
			return Either.left(high.wrap(cx, recipe, from));
		} else if (low.hasPriority(cx, recipe, from)) {
			// if low has priority, only try to read low
			return Either.right(low.wrap(cx, recipe, from));
		} else {
			// If neither has priority, try to read high, if it fails, fallback to low
			try {
				return Either.left(high.wrap(cx, recipe, from));
			} catch (Exception ex1) {
				try {
					return Either.right(low.wrap(cx, recipe, from));
				} catch (Exception ex2) {
					ConsoleJS.SERVER.warn("Failed to read %s as high priority (%s)!".formatted(from, high), ex1);
					ConsoleJS.SERVER.warn("Failed to read %s as low priority (%s)!".formatted(from, low), ex2);
					throw new KubeRuntimeException("Failed to read %s as either %s or %s!".formatted(from, high, low)).source(recipe.sourceLine);
				}
			}
		}
	}

	@Override
	public boolean matches(Context cx, KubeRecipe recipe, Either<H, L> value, ReplacementMatchInfo match) {
		var l = value.left();
		return l.isPresent() ? high.matches(cx, recipe, l.get(), match) : low.matches(cx, recipe, value.right().get(), match);
	}

	@Override
	public Either<H, L> replace(Context cx, KubeRecipe recipe, Either<H, L> original, ReplacementMatchInfo match, Object with) {
		var l = original.left();

		if (l.isPresent()) {
			var r = high.replace(cx, recipe, l.get(), match, with);
			return r == l.get() ? original : Either.left(r);
		} else {
			var r = low.replace(cx, recipe, original.right().get(), match, with);
			return r == original.right().get() ? original : Either.right(r);
		}
	}

	@Override
	public void buildUniqueId(UniqueIdBuilder builder, Either<H, L> value) {
		var left = value.left();

		if (left.isPresent()) {
			high.buildUniqueId(builder, left.get());
		} else {
			low.buildUniqueId(builder, value.right().get());
		}
	}

	@Override
	public boolean isEmpty(Either<H, L> value) {
		return value.map(high::isEmpty, low::isEmpty);
	}

	@Override
	public String toString() {
		return "either<" + high + ", " + low + ">";
	}
}
