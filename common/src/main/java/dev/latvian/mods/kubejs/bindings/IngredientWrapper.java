package dev.latvian.mods.kubejs.bindings;

import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientWithCustomPredicateJS;
import dev.latvian.mods.kubejs.item.ingredient.MatchAllIngredientJS;
import dev.latvian.mods.kubejs.item.ingredient.MatchAnyIngredientJS;
import dev.latvian.mods.kubejs.recipe.RecipeEventJS;
import dev.latvian.mods.kubejs.recipe.ingredientaction.CustomIngredientAction;
import dev.latvian.mods.kubejs.recipe.ingredientaction.CustomIngredientActionCallback;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
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

	public static IngredientJS custom(IngredientJS in, Predicate<ItemStackJS> predicate) {
		if (RecipeEventJS.customIngredientMap != null) {
			IngredientWithCustomPredicateJS ingredient = new IngredientWithCustomPredicateJS(UUID.randomUUID(), in, i -> predicate.test(new ItemStackJS(i)));
			RecipeEventJS.customIngredientMap.put(ingredient.uuid, ingredient);
			return ingredient;
		}

		return new IngredientWithCustomPredicateJS(null, in, i -> predicate.test(new ItemStackJS(i)));
	}

	public static IngredientJS customNBT(IngredientJS in, Predicate<CompoundTag> predicate) {
		return custom(in, is -> is.hasNBT() && predicate.test(is.getNbt()));
	}

	public static IngredientJS matchAny(Object objects) {
		MatchAnyIngredientJS ingredient = new MatchAnyIngredientJS();
		ingredient.addAll(objects);
		return ingredient;
	}

	public static void registerCustomIngredientAction(String id, CustomIngredientActionCallback callback) {
		CustomIngredientAction.MAP.put(id, callback);
	}

	public static boolean isIngredient(@Nullable Object o) {
		return o instanceof IngredientJS;
	}
}