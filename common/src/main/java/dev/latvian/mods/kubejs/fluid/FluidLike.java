package dev.latvian.mods.kubejs.fluid;

import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;

@RemapPrefixForJS("kjs$")
public interface FluidLike extends ReplacementMatch {
	long kjs$getAmount();

	default boolean kjs$isEmpty() {
		return kjs$getAmount() <= 0L;
	}

	default FluidLike kjs$copy(long amount) {
		return this;
	}

	default boolean matches(FluidLike other) {
		return equals(other);
	}
}
