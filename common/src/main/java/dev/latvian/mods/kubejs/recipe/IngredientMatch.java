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
			getAllItems();
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

		for (var stack : getAllItemArray()) {
			if (in.test(stack)) {
				return true;
			}
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
