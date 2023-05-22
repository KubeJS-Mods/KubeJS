package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import dev.latvian.mods.kubejs.core.RecipeKJS;
import dev.latvian.mods.kubejs.recipe.InputReplacement;
import dev.latvian.mods.kubejs.recipe.OutputReplacement;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.kubejs.util.MutableBoolean;

public record EitherRecipeComponent<H, L>(RecipeComponent<H> high, RecipeComponent<L> low) implements RecipeComponent<Either<H, L>> {
	public static <H, L> EitherRecipeComponent<H, L> of(RecipeComponent<H> highPriority, RecipeComponent<L> lowPriority) {
		return new EitherRecipeComponent<>(highPriority, lowPriority);
	}

	@Override
	public String componentType() {
		return "either";
	}

	@Override
	public JsonObject description() {
		var obj = new JsonObject();
		obj.addProperty("type", componentType());
		obj.add("high_priority", high.description());
		obj.add("low_priority", low.description());
		return obj;
	}

	@Override
	public RecipeComponentType getType() {
		if (high.getType() == RecipeComponentType.OTHER) {
			return low.getType();
		}

		return high.getType();
	}

	@Override
	public JsonElement write(Either<H, L> value) {
		if (value.left().isPresent()) {
			return high.write(value.left().get());
		} else {
			return low.write(value.right().get());
		}
	}

	@Override
	public Either<H, L> read(Object from) {
		if (high.shouldRead(from)) {
			return Either.left(high.read(from));
		} else {
			return Either.right(low.read(from));
		}
	}

	@Override
	public boolean hasInput(RecipeKJS recipe, Either<H, L> value, ReplacementMatch match) {
		var l = value.left();
		return l.isPresent() ? high.hasInput(recipe, l.get(), match) : low.hasInput(recipe, value.right().get(), match);
	}

	@Override
	public Either<H, L> replaceInput(RecipeKJS recipe, Either<H, L> value, ReplacementMatch match, InputReplacement with, MutableBoolean changed) {
		var l = value.left();
		return l.isPresent() ? Either.left(high.replaceInput(recipe, l.get(), match, with, changed)) : Either.right(low.replaceInput(recipe, value.right().get(), match, with, changed));
	}

	@Override
	public boolean hasOutput(RecipeKJS recipe, Either<H, L> value, ReplacementMatch match) {
		var l = value.left();
		return l.isPresent() ? high.hasOutput(recipe, l.get(), match) : low.hasOutput(recipe, value.right().get(), match);
	}

	@Override
	public Either<H, L> replaceOutput(RecipeKJS recipe, Either<H, L> value, ReplacementMatch match, OutputReplacement with, MutableBoolean changed) {
		var l = value.left();
		return l.isPresent() ? Either.left(high.replaceOutput(recipe, l.get(), match, with, changed)) : Either.right(low.replaceOutput(recipe, value.right().get(), match, with, changed));
	}

	@Override
	public String toString() {
		return "either{" + high + "|" + low + "}";
	}
}
