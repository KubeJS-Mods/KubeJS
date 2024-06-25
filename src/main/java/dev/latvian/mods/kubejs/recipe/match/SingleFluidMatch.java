package dev.latvian.mods.kubejs.recipe.match;

import dev.latvian.mods.rhino.Context;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;

public record SingleFluidMatch(FluidStack stack) implements FluidMatch {
	@Override
	public boolean matches(Context cx, FluidStack s, boolean exact) {
		return stack.getFluid() == s.getFluid();
	}

	@Override
	public boolean matches(Context cx, FluidIngredient ingredient, boolean exact) {
		return ingredient.test(stack);
	}

	@Override
	public String toString() {
		return stack.getFluid().kjs$getId();
	}
}
