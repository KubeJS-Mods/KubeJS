package dev.latvian.mods.kubejs.recipe.component;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.recipe.filter.RecipeMatchContext;
import dev.latvian.mods.kubejs.recipe.match.ReplacementMatchInfo;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import dev.latvian.mods.kubejs.util.OpsContainer;
import dev.latvian.mods.rhino.type.TypeInfo;

import java.util.List;

@SuppressWarnings("OptionalIsPresent")
public record EitherRecipeComponent<H, L>(RecipeComponent<H> left, RecipeComponent<L> right, Codec<Either<H, L>> codec, TypeInfo typeInfo) implements RecipeComponent<Either<H, L>> {
	public static final RecipeComponentType<?> TYPE = RecipeComponentType.<EitherRecipeComponent<?, ?>>dynamic(KubeJS.id("either"), (type, ctx) -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		ctx.recipeComponentCodec().fieldOf("left").forGetter(EitherRecipeComponent::left),
		ctx.recipeComponentCodec().fieldOf("right").forGetter(EitherRecipeComponent::right)
	).apply(instance, EitherRecipeComponent::new)));

	public EitherRecipeComponent(RecipeComponent<H> left, RecipeComponent<L> right) {
		this(left, right, Codec.either(left.codec(), right.codec()), left.typeInfo().or(right.typeInfo()));
	}

	@Override
	public RecipeComponentType<?> type() {
		return TYPE;
	}

	@Override
	public Either<H, L> wrap(RecipeScriptContext cx, Object from) {
		if (left.hasPriority(cx, from)) {
			// if left has priority, only try to read left
			var value = left.wrap(cx, from);

			if (left.allowEmpty() || !left.isEmpty(value)) {
				return Either.left(value);
			}
		}

		if (right.hasPriority(cx, from)) {
			// if right has priority, only try to read right
			var value = right.wrap(cx, from);

			if (right.allowEmpty() || !right.isEmpty(value)) {
				return Either.right(value);
			}
		}

		try {
			// If neither has priority, try to read left, if it fails, fallback to right
			var value = left.wrap(cx, from);

			if (left.allowEmpty() || !left.isEmpty(value)) {
				left.validate(cx, value);
				return Either.left(value);
			}
		} catch (Exception ex1) {
			try {
				var value = right.wrap(cx, from);

				if (right.allowEmpty() || !right.isEmpty(value)) {
					// right.validate(cx, value);
					return Either.right(value);
				}
			} catch (Exception ex2) {
				ConsoleJS.SERVER.warn("Failed to read %s (left: %s)!".formatted(from, left), ex1);
				ConsoleJS.SERVER.warn("Failed to read %s (right: %s)!".formatted(from, right), ex2);
			}
		}

		throw new KubeRuntimeException("Failed to read %s as either %s or %s!".formatted(from, left, right)).source(cx.recipe().sourceLine);
	}

	@Override
	public boolean matches(RecipeMatchContext cx, Either<H, L> value, ReplacementMatchInfo match) {
		var l = value.left();
		return l.isPresent() ? left.matches(cx, l.get(), match) : right.matches(cx, value.right().get(), match);
	}

	@Override
	public Either<H, L> replace(RecipeScriptContext cx, Either<H, L> original, ReplacementMatchInfo match, Object with) {
		var l = original.left();

		if (l.isPresent()) {
			var r = left.replace(cx, l.get(), match, with);
			return r == l.get() ? original : Either.left(r);
		} else {
			var r = right.replace(cx, original.right().get(), match, with);
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
	public void validate(RecipeValidationContext ctx, Either<H, L> value) {
		ctx.errors().push(this);

		var l = value.left();

		if (l.isPresent()) {
			ctx.errors().setKey("left");
			left.validate(ctx, l.get());
		} else {
			ctx.errors().setKey("right");
			right.validate(ctx, value.right().get());
		}

		ctx.errors().pop();
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
