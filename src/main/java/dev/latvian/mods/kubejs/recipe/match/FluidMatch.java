package dev.latvian.mods.kubejs.recipe.match;

import dev.latvian.mods.rhino.Context;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;

public interface FluidMatch extends ReplacementMatch {
	boolean matches(Context cx, FluidStack stack, boolean exact);

	boolean matches(Context cx, FluidIngredient ingredient, boolean exact);
}
