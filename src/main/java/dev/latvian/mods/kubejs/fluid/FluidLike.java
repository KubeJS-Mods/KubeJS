package dev.latvian.mods.kubejs.fluid;

import dev.latvian.mods.kubejs.recipe.match.ReplacementMatch;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.world.level.material.Fluid;

@RemapPrefixForJS("kjs$")
public interface FluidLike extends ReplacementMatch {
	int kjs$getAmount();

	Fluid kjs$getFluid();

	default boolean kjs$isEmpty() {
		return kjs$getAmount() <= 0;
	}

	default FluidLike kjs$copy(int amount) {
		return this;
	}
}
