package dev.latvian.mods.kubejs.recipe.match;

import dev.latvian.mods.kubejs.bindings.IngredientWrapper;
import dev.latvian.mods.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.mods.rhino.Context;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public record IngredientMatch(Ingredient ingredient) implements ItemMatch {
	@Override
	public boolean matches(Context cx, ItemStack item, boolean exact) {
		if (item.isEmpty()) {
			return false;
		} else if (exact) {
			var stacks = ingredient.kjs$getStacks();
			return stacks.size() == 1 && ItemStack.isSameItemSameComponents(stacks.getFirst(), item);
		} else {
			return ingredient.test(item);
		}
	}

	@Override
	public boolean matches(Context cx, Ingredient in, boolean exact) {
		if (in == Ingredient.EMPTY) {
			return false;
		}

		if (exact) {
			var t1 = IngredientWrapper.tagKeyOf(ingredient);
			var t2 = IngredientWrapper.tagKeyOf(in);

			if (t1 != null && t2 != null) {
				return t1 == t2;
			} else {
				return ingredient.equals(in);
			}
		}

		try {
			for (var stack : in.getItems()) {
				if (ingredient.test(stack)) {
					return true;
				}
			}
		} catch (Exception ex) {
			throw new RecipeExceptionJS("Failed to test ingredient " + in, ex);
		}

		return false;
	}

	@Override
	public String toString() {
		return ingredient.toString();
	}
}
