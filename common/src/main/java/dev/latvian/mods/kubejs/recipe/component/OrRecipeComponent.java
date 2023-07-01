package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Either;
import dev.latvian.mods.kubejs.recipe.InputReplacement;
import dev.latvian.mods.kubejs.recipe.OutputReplacement;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.kubejs.typings.desc.DescriptionContext;
import dev.latvian.mods.kubejs.typings.desc.TypeDescJS;

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
	public JsonElement write(RecipeJS recipe, Either<H, L> value) {
		if (value.left().isPresent()) {
			return high.write(recipe, value.left().get());
		} else {
			return low.write(recipe, value.right().get());
		}
	}

	@Override
	public Either<H, L> read(RecipeJS recipe, Object from) {
		if (high.hasPriority(recipe, from)) {
			return Either.left(high.read(recipe, from));
		} else {
			return Either.right(low.read(recipe, from));
		}
	}

	@Override
	public boolean isInput(RecipeJS recipe, Either<H, L> value, ReplacementMatch match) {
		var l = value.left();
		return l.isPresent() ? high.isInput(recipe, l.get(), match) : low.isInput(recipe, value.right().get(), match);
	}

	@Override
	public Either<H, L> replaceInput(RecipeJS recipe, Either<H, L> original, ReplacementMatch match, InputReplacement with) {
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
	public boolean isOutput(RecipeJS recipe, Either<H, L> value, ReplacementMatch match) {
		var l = value.left();
		return l.isPresent() ? high.isOutput(recipe, l.get(), match) : low.isOutput(recipe, value.right().get(), match);
	}

	@Override
	public Either<H, L> replaceOutput(RecipeJS recipe, Either<H, L> original, ReplacementMatch match, OutputReplacement with) {
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
	public String toString() {
		return "{" + high + "|" + low + "}";
	}
}
