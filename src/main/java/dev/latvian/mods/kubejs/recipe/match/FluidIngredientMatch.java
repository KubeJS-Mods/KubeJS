package dev.latvian.mods.kubejs.recipe.match;

import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.rhino.Context;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;

public record FluidIngredientMatch(FluidIngredient ingredient) implements FluidMatch {
	@Override
	public boolean matches(Context cx, FluidStack s, boolean exact) {
		return !s.isEmpty() && ingredient.test(s);
	}

	@Override
	public boolean matches(Context cx, FluidIngredient in, boolean exact) {
		if (in == FluidIngredient.empty()) {
			return false;
		}

		try {
			for (var stack : ingredient.getStacks()) {
				if (in.test(stack)) {
					return true;
				}
			}
		} catch (Exception ex) {
			throw new KubeRuntimeException("Failed to test fluid ingredient " + in, ex);
		}

		return false;
	}

	@Override
	public String toString() {
		return ingredient.toString();
	}
}
