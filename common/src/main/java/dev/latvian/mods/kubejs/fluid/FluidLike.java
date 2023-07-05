package dev.latvian.mods.kubejs.fluid;

import dev.latvian.mods.kubejs.recipe.ReplacementMatch;

public interface FluidLike extends ReplacementMatch {
	long getAmount();

	default boolean isEmpty() {
		return getAmount() <= 0L;
	}

	default FluidLike copy(long amount) {
		return this;
	}

	default boolean matches(FluidLike other) {
		return equals(other);
	}
}
