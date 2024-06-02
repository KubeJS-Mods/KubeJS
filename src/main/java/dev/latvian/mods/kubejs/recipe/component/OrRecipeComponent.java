package dev.latvian.mods.kubejs.recipe.component;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.recipe.InputReplacement;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.OutputReplacement;
import dev.latvian.mods.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;

@SuppressWarnings("OptionalIsPresent")
public record OrRecipeComponent<H, L>(RecipeComponent<H> high, RecipeComponent<L> low) implements RecipeComponent<Either<H, L>> {
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
					ConsoleJS.SERVER.error("Failed to read %s as high priority (%s)!".formatted(from, high), ex1);
					ConsoleJS.SERVER.error("Failed to read %s as low priority (%s)!".formatted(from, low), ex2);
					throw new RecipeExceptionJS("Failed to read %s as either %s or %s!".formatted(from, high, low));
				}
			}
		}
	}

	@Override
	public boolean isInput(KubeRecipe recipe, Either<H, L> value, ReplacementMatch match) {
		var l = value.left();
		return l.isPresent() ? high.isInput(recipe, l.get(), match) : low.isInput(recipe, value.right().get(), match);
	}

	@Override
	public Either<H, L> replaceInput(Context cx, KubeRecipe recipe, Either<H, L> original, ReplacementMatch match, InputReplacement with) {
		var l = original.left();

		if (l.isPresent()) {
			var r = high.replaceInput(cx, recipe, l.get(), match, with);
			return r == l.get() ? original : Either.left(r);
		} else {
			var r = low.replaceInput(cx, recipe, original.right().get(), match, with);
			return r == original.right().get() ? original : Either.right(r);
		}
	}

	@Override
	public boolean isOutput(KubeRecipe recipe, Either<H, L> value, ReplacementMatch match) {
		var l = value.left();
		return l.isPresent() ? high.isOutput(recipe, l.get(), match) : low.isOutput(recipe, value.right().get(), match);
	}

	@Override
	public Either<H, L> replaceOutput(Context cx, KubeRecipe recipe, Either<H, L> original, ReplacementMatch match, OutputReplacement with) {
		var l = original.left();

		if (l.isPresent()) {
			var r = high.replaceOutput(cx, recipe, l.get(), match, with);
			return r == l.get() ? original : Either.left(r);
		} else {
			var r = low.replaceOutput(cx, recipe, original.right().get(), match, with);
			return r == original.right().get() ? original : Either.right(r);
		}
	}

	@Override
	public boolean checkValueHasChanged(Either<H, L> oldValue, Either<H, L> newValue) {
		if (oldValue != null && newValue != null) {
			var left = oldValue.left();

			if (left.isPresent()) {
				if (high.checkValueHasChanged(left.get(), newValue.left().get())) {
					return true;
				}
			} else if (low.checkValueHasChanged(oldValue.right().get(), newValue.right().get())) {
				return true;
			}
		}

		return oldValue != newValue;
	}

	@Override
	public String toString() {
		return "either<" + high + ", " + low + ">";
	}
}
