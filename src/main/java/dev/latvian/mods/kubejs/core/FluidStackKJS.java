package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.fluid.FluidLike;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import dev.latvian.mods.kubejs.recipe.OutputReplacement;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.rhino.Context;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

public interface FluidStackKJS extends OutputReplacement, FluidLike {
	default FluidStack kjs$self() {
		return (FluidStack) (Object) this;
	}

	@Override
	default int kjs$getAmount() {
		return kjs$self().getAmount();
	}

	@Override
	default boolean kjs$isEmpty() {
		return kjs$self().isEmpty();
	}

	@Override
	default Fluid kjs$getFluid() {
		return kjs$self().getFluid();
	}

	@Override
	default FluidLike kjs$copy(int amount) {
		return (FluidLike) (Object) kjs$self().copyWithAmount(amount);
	}

	@Override
	default Object replaceOutput(Context cx, KubeRecipe recipe, ReplacementMatch match, OutputReplacement original) {
		if (original instanceof FluidLike o) {
			return kjs$copy(o.kjs$getAmount());
		}

		return kjs$copy(kjs$getAmount());
	}
}
