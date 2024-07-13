package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.recipe.match.FluidMatch;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;

@RemapPrefixForJS("kjs$")
public interface FluidIngredientKJS extends FluidMatch {
	@Override
	default boolean matches(Context cx, FluidStack s, boolean exact) {
		return !s.isEmpty() && ((FluidIngredient) this).test(s);
	}

	@Override
	default boolean matches(Context cx, FluidIngredient in, boolean exact) {
		if (in == FluidIngredient.empty()) {
			return false;
		}

		try {
			for (var stack : ((FluidIngredient) this).getStacks()) {
				if (in.test(stack)) {
					return true;
				}
			}
		} catch (Exception ex) {
			throw new KubeRuntimeException("Failed to test fluid ingredient " + in, ex);
		}

		return false;
	}
}
