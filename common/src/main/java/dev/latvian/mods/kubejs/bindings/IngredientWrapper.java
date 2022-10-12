package dev.latvian.mods.kubejs.bindings;

import dev.latvian.mods.kubejs.item.ingredient.IngredientWithCustomPredicate;
import dev.latvian.mods.kubejs.platform.IngredientPlatformHelper;
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
	Ingredient all = IngredientPlatformHelper.get().wildcard();

	static Ingredient of(Ingredient ingredient) {
		return ingredient;
	}

	static Ingredient of(Ingredient ingredient, int count) {
		return ingredient.kjs$withCount(count);
	}

	static Ingredient custom(Ingredient parent, Predicate<ItemStack> predicate) {
		if (RecipesEventJS.customIngredientMap != null) {
			var ingredient = new IngredientWithCustomPredicate(parent, UUID.randomUUID(), predicate);
			RecipesEventJS.customIngredientMap.put(ingredient.uuid, ingredient);
			return IngredientPlatformHelper.get().custom(parent, ingredient.uuid);
		}

		return IngredientPlatformHelper.get().custom(parent, predicate);
	}

	static Ingredient custom(Predicate<ItemStack> predicate) {
		return custom(all, predicate);
	}

	static Ingredient customNBT(Ingredient in, Predicate<CompoundTag> predicate) {
		return custom(in, is -> is.hasTag() && predicate.test(is.getTag()));
	}

	static void registerCustomIngredientAction(String id, CustomIngredientActionCallback callback) {
		CustomIngredientAction.MAP.put(id, callback);
	}

	static boolean isIngredient(@Nullable Object o) {
		return o instanceof Ingredient;
	}
}