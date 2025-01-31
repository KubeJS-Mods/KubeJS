package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.plugin.builtin.wrapper.IngredientWrapper;
import dev.latvian.mods.kubejs.recipe.match.ItemMatch;
import dev.latvian.mods.kubejs.recipe.match.Replaceable;
import dev.latvian.mods.rhino.Context;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

public interface SizedIngredientKJS extends Replaceable, IngredientSupplierKJS, ItemMatch {
	default SizedIngredient kjs$self() {
		return (SizedIngredient) (Object) this;
	}

	@Override
	default Object replaceThisWith(Context cx, Object with) {
		var ingredient = IngredientWrapper.wrap(cx, with);

		if (!ingredient.equals(kjs$self().ingredient())) {
			return new SizedIngredient(ingredient, kjs$self().count());
		}

		return this;
	}

	@Override
	default Ingredient kjs$asIngredient() {
		return kjs$self().ingredient();
	}

	@Override
	default boolean matches(Context cx, ItemStack item, boolean exact) {
		return kjs$self().ingredient().matches(cx, item, exact);
	}

	@Override
	default boolean matches(Context cx, Ingredient in, boolean exact) {
		return kjs$self().ingredient().matches(cx, in, exact);
	}
}
