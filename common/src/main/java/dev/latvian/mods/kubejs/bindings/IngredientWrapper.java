package dev.latvian.mods.kubejs.bindings;

import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.ingredient.IngredientWithCustomPredicate;
import dev.latvian.mods.kubejs.platform.IngredientPlatformHelper;
import dev.latvian.mods.kubejs.recipe.RecipesEventJS;
import dev.latvian.mods.kubejs.recipe.ingredientaction.CustomIngredientAction;
import dev.latvian.mods.kubejs.recipe.ingredientaction.CustomIngredientActionCallback;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Predicate;

@Info("Various Ingredient related helper methods")
public interface IngredientWrapper {
	@Info("A completely empty ingredient that will only match air")
	Ingredient none = Ingredient.EMPTY;
	@Info("An ingredient that matches everything")
	Ingredient all = IngredientPlatformHelper.get().wildcard();

	@Info("Returns an ingredient of the input")
	static Ingredient of(Ingredient ingredient) {
		return ingredient;
	}

	@Info("Returns an ingredient of the input, with the specified count")
	static InputItem of(Ingredient ingredient, int count) {
		return ingredient.kjs$withCount(count);
	}

	@Info("Make a custom ingredient where items must match both the parent ingredient and the custom predicate function")
	static Ingredient custom(Ingredient parent, Predicate<ItemStack> predicate) {
		if (RecipesEventJS.customIngredientMap != null) {
			var ingredient = new IngredientWithCustomPredicate(parent, UUID.randomUUID(), predicate);
			RecipesEventJS.customIngredientMap.put(ingredient.uuid, ingredient);
			return IngredientPlatformHelper.get().custom(parent, ingredient.uuid);
		}

		return IngredientPlatformHelper.get().custom(parent, predicate);
	}

	@Info("Make a custom ingredient where a match must match the provided predicate function")
	static Ingredient custom(Predicate<ItemStack> predicate) {
		return custom(all, predicate);
	}

	@Info("Make a custom ingredient where an item must match both the parent ingredient and the item's nbt must match the custom predicate function")
	static Ingredient customNBT(Ingredient in, Predicate<CompoundTag> predicate) {
		return custom(in, is -> is.hasTag() && predicate.test(is.getTag()));
	}

	@Info("Register a custom ingredient action for use in recipes with Recipe#customIngredientAction")
	static void registerCustomIngredientAction(String id, CustomIngredientActionCallback callback) {
		CustomIngredientAction.MAP.put(id, callback);
	}

	@Info("""
		Checks if the passed in object is an Ingredient.
		Note that this does not mean it will not function as an Ingredient if passed to something that requests one.
		""")
	static boolean isIngredient(@Nullable Object o) {
		return o instanceof Ingredient;
	}
}