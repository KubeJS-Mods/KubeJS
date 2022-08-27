package dev.latvian.mods.kubejs.bindings;

import dev.latvian.mods.kubejs.item.ingredient.CustomIngredient;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientWithCustomPredicate;
import dev.latvian.mods.kubejs.item.ingredient.WildcardIngredient;
import dev.latvian.mods.kubejs.recipe.RecipesEventJS;
import dev.latvian.mods.kubejs.recipe.ingredientaction.CustomIngredientAction;
import dev.latvian.mods.kubejs.recipe.ingredientaction.CustomIngredientActionCallback;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Predicate;

/**
 * @author LatvianModder
 */
public interface IngredientWrapper {
	Ingredient none = Ingredient.EMPTY;
	Ingredient all = WildcardIngredient.INSTANCE;

	static Ingredient of(Object object) {
		return IngredientJS.of(object);
	}

	static Ingredient custom(Ingredient in, Predicate<ItemStack> predicate) {
		if (RecipesEventJS.customIngredientMap != null) {
			var ingredient = new IngredientWithCustomPredicate(UUID.randomUUID(), in, predicate);
			RecipesEventJS.customIngredientMap.put(ingredient.uuid, ingredient);
			return ingredient;
		}

		return new IngredientWithCustomPredicate(null, in, predicate);
	}

	static Ingredient customNBT(Ingredient in, Predicate<CompoundTag> predicate) {
		return custom(in, is -> is.hasTag() && predicate.test(is.getTag()));
	}

	static Ingredient of(Object object, int count) {
		return of(object).kjs$withCount(Math.max(1, count));
	}

	static Ingredient custom(Predicate<ItemStack> predicate) {
		return new CustomIngredient(predicate);
	}

	static void registerCustomIngredientAction(String id, CustomIngredientActionCallback callback) {
		CustomIngredientAction.MAP.put(id, callback);
	}

	static boolean isIngredient(@Nullable Object o) {
		return o instanceof IngredientJS;
	}
}