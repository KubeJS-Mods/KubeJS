package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Either;
import dev.latvian.mods.kubejs.recipe.InputReplacement;
import dev.latvian.mods.kubejs.recipe.OutputReplacement;
import dev.latvian.mods.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.kubejs.typings.desc.DescriptionContext;
import dev.latvian.mods.kubejs.typings.desc.TypeDescJS;
import dev.latvian.mods.kubejs.util.ConsoleJS;

@SuppressWarnings("OptionalIsPresent")
public record OrRecipeComponent<H, L>(RecipeComponent<H> high, RecipeComponent<L> low) implements RecipeComponent<Either<H, L>> {
	@Override
	public String componentType() {
		return "or";
	}

	@Override
	public TypeDescJS constructorDescription(DescriptionContext ctx) {
		return high.constructorDescription(ctx).or(low.constructorDescription(ctx));
	}

	@Override
	public ComponentRole role() {
		if (high.role().isOther()) {
			return low.role();
		}

		return high.role();
	}

	@Override
	public Class<?> componentClass() {
		return Either.class;
	}

	@Override
	public JsonElement write(KubeRecipe recipe, Either<H, L> value) {
		if (value.left().isPresent()) {
			return high.write(recipe, value.left().get());
		} else {
			return low.write(recipe, value.right().orElseThrow());
		}
	}

	@Override
	public Either<H, L> read(KubeRecipe recipe, Object from) {
		if (high.hasPriority(recipe, from)) {
			// if high has priority, only try to read high
			return Either.left(high.read(recipe, from));
		} else if (low.hasPriority(recipe, from)) {
			// if low has priority, only try to read low
			return Either.right(low.read(recipe, from));
		} else {
			// If neither has priority, try to read high, if it fails, fallback to low
			try {
				return Either.left(high.read(recipe, from));
			} catch (Exception ex1) {
				try {
					return Either.right(low.read(recipe, from));
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
	public Either<H, L> replaceInput(KubeRecipe recipe, Either<H, L> original, ReplacementMatch match, InputReplacement with) {
		var l = original.left();

		if (l.isPresent()) {
			var r = high.replaceInput(recipe, l.get(), match, with);
			return r == l.get() ? original : Either.left(r);
		} else {
			var r = low.replaceInput(recipe, original.right().get(), match, with);
			return r == original.right().get() ? original : Either.right(r);
		}
	}

	@Override
	public boolean isOutput(KubeRecipe recipe, Either<H, L> value, ReplacementMatch match) {
		var l = value.left();
		return l.isPresent() ? high.isOutput(recipe, l.get(), match) : low.isOutput(recipe, value.right().get(), match);
	}

	@Override
	public Either<H, L> replaceOutput(KubeRecipe recipe, Either<H, L> original, ReplacementMatch match, OutputReplacement with) {
		var l = original.left();

		if (l.isPresent()) {
			var r = high.replaceOutput(recipe, l.get(), match, with);
			return r == l.get() ? original : Either.left(r);
		} else {
			var r = low.replaceOutput(recipe, original.right().get(), match, with);
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
		return "{" + high + "|" + low + "}";
	}
}
