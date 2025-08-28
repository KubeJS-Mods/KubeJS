package dev.latvian.mods.kubejs.recipe.match;

import dev.latvian.mods.kubejs.recipe.filter.RecipeMatchContext;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;

public interface FluidMatch extends ReplacementMatch {
	boolean matches(RecipeMatchContext cx, FluidStack stack, boolean exact);

	boolean matches(RecipeMatchContext cx, FluidIngredient ingredient, boolean exact);
}
