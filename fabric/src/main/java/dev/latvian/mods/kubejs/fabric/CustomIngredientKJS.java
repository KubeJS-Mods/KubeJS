package dev.latvian.mods.kubejs.fabric;


import dev.latvian.mods.kubejs.core.IngredientKJS;

// tech if you read this, i am so sorry for this
public interface CustomIngredientKJS {

	/**
	 * Basically a mirror of {@link IngredientKJS#kjs$canBeUsedForMatching()}.
	 * Same rules apply as for that, but since CustomIngredient is only non-null if
	 * the Ingredient isn't standard, this is always false by default.
	 */
	default boolean kjs$canBeUsedForMatching() {
		return false;
	}
}
