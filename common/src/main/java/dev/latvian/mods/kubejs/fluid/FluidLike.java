package dev.latvian.mods.kubejs.fluid;

import dev.latvian.mods.kubejs.recipe.ReplacementMatch;

public interface FluidLike extends ReplacementMatch {
	default boolean isEmpty() {
		return getAmount() <= 0L;
	}

	long getAmount();

	FluidLike copy(long amount);

	default boolean matches(FluidLike other) {
		return equals(other);
	}
}
