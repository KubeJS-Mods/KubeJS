package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.fluid.FluidWrapper;
import dev.latvian.mods.kubejs.recipe.RecipeScriptContext;
import dev.latvian.mods.kubejs.recipe.filter.RecipeMatchContext;
import dev.latvian.mods.kubejs.recipe.match.FluidMatch;
import dev.latvian.mods.kubejs.recipe.match.Replaceable;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

@RemapPrefixForJS("kjs$")
public interface SizedFluidIngredientKJS extends Replaceable, FluidMatch {
	default SizedFluidIngredient kjs$self() {
		return (SizedFluidIngredient) (Object) this;
	}

	@Override
	default Object replaceThisWith(RecipeScriptContext cx, Object with) {
		var ingredient = FluidWrapper.wrapIngredient(cx.registries(), with);

		if (!ingredient.equals(kjs$self().ingredient())) {
			return new SizedFluidIngredient(ingredient, kjs$self().amount());
		}

		return this;
	}

	@Override
	default boolean matches(RecipeMatchContext cx, FluidStack s, boolean exact) {
		return ((FluidMatch) kjs$self().ingredient()).matches(cx, s, exact);
	}

	@Override
	default boolean matches(RecipeMatchContext cx, FluidIngredient in, boolean exact) {
		return ((FluidMatch) kjs$self().ingredient()).matches(cx, in, exact);
	}
}
