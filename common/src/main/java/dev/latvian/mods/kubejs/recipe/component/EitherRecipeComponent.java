package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;

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
	public String toString() {
		return "either{" + high + "|" + low + "}";
	}
}
