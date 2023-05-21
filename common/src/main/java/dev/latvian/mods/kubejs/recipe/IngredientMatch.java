package dev.latvian.mods.kubejs.recipe;

import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.ItemStackSet;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class IngredientMatch {
	public static IngredientMatch of(Object o) {
		// fixme, add exact: true/false support
		return new IngredientMatch(IngredientJS.of(o), false);
	}

	public final Ingredient ingredient;
	public final boolean exact;
	private ItemStackSet allItems;
	private ItemStack[] allItemsArray;

	public IngredientMatch(Ingredient ingredient, boolean exact) {
		this.ingredient = ingredient;
		this.exact = exact;
	}

	public ItemStackSet getAllItems() {
		if (allItems == null) {
			allItems = ingredient.kjs$getStacks();
			allItemsArray = allItems.toArray();
		}

		return allItems;
	}

	public ItemStack[] getAllItemArray() {
		if (allItemsArray == null) {
			// It's apparently very important that this does not reference getAllItems() directly because of probably JVM bug:
			// Cannot read the array length because "<local2>" is null
			allItems = ingredient.kjs$getStacks();
			allItemsArray = allItems.toArray();
		}

		return allItemsArray;
	}

	public boolean contains(ItemStack item) {
		return !item.isEmpty() && getAllItems().contains(item);
	}

	public boolean contains(Ingredient in) {
		if (in == Ingredient.EMPTY) {
			return false;
		}

		try {
			for (var stack : getAllItemArray()) {
				if (in.test(stack)) {
					return true;
				}
			}
		} catch (Exception ex) {
			throw new RecipeExceptionJS("Failed to test ingredient " + in, ex);
		}

		return false;
	}

	public boolean contains(InputItem in) {
		return contains(in.ingredient);
	}

	public boolean contains(OutputItem out) {
		return contains(out.item);
	}
}
