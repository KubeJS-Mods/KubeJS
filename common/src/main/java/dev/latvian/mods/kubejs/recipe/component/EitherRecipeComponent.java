package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import dev.latvian.mods.kubejs.recipe.InputReplacement;
import dev.latvian.mods.kubejs.recipe.OutputReplacement;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;

public record EitherRecipeComponent<H, L>(RecipeComponent<H> high, RecipeComponent<L> low) implements RecipeComponent<Either<H, L>> {
	public static <H, L> EitherRecipeComponent<H, L> of(RecipeComponent<H> highPriority, RecipeComponent<L> lowPriority) {
		return new EitherRecipeComponent<>(highPriority, lowPriority);
	}

	@Override
	public String componentType() {
		return "either";
	}

	@Override
	public JsonObject description(RecipeJS recipe) {
		var obj = new JsonObject();
		obj.addProperty("type", componentType());
		obj.add("high_priority", high.description(recipe));
		obj.add("low_priority", low.description(recipe));
		return obj;
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
	public Either<H, L> replaceInput(RecipeJS recipe, Either<H, L> value, ReplacementMatch match, InputReplacement with) {
		var l = value.left();

		if (l.isPresent()) {
			var r = high.replaceInput(recipe, l.get(), match, with);
			return r == l.get() ? value : Either.left(r);
		} else {
			var r = low.replaceInput(recipe, value.right().get(), match, with);
			return r == value.right().get() ? value : Either.right(r);
		}
	}

	@Override
	public boolean isOutput(RecipeJS recipe, Either<H, L> value, ReplacementMatch match) {
		var l = value.left();
		return l.isPresent() ? high.isOutput(recipe, l.get(), match) : low.isOutput(recipe, value.right().get(), match);
	}

	@Override
	public Either<H, L> replaceOutput(RecipeJS recipe, Either<H, L> value, ReplacementMatch match, OutputReplacement with) {
		var l = value.left();

		if (l.isPresent()) {
			var r = high.replaceOutput(recipe, l.get(), match, with);
			return r == l.get() ? value : Either.left(r);
		} else {
			var r = low.replaceOutput(recipe, value.right().get(), match, with);
			return r == value.right().get() ? value : Either.right(r);
		}
	}

	@Override
	public String toString() {
		return "either{" + high + "|" + low + "}";
	}
}
