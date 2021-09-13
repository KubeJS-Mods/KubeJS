package dev.latvian.kubejs.bindings;

import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.item.ingredient.MatchAllIngredientJS;
import dev.latvian.kubejs.item.ingredient.MatchAnyIngredientJS;

import java.util.function.Predicate;

/**
 * @author LatvianModder
 */
public class IngredientWrapper {
	public static IngredientJS getNone() {
		return ItemStackJS.EMPTY;
	}

	public static IngredientJS getAll() {
		return MatchAllIngredientJS.INSTANCE;
	}

	public static IngredientJS of(Object object) {
		return IngredientJS.of(object);
	}

	public static IngredientJS of(Object object, int count) {
		return of(object).withCount(Math.max(1, count));
	}

	public static IngredientJS custom(Predicate<ItemStackJS> predicate) {
		return predicate::test;
	}

	public static IngredientJS matchAny(Object objects) {
		MatchAnyIngredientJS ingredient = new MatchAnyIngredientJS();
		ingredient.addAll(objects);
		return ingredient;
	}
}