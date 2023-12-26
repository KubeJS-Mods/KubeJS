package dev.latvian.mods.kubejs.recipe;

import dev.latvian.mods.kubejs.item.ItemStackSet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class IngredientMatch implements ItemMatch {
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

	@Override
	public boolean contains(ItemStack item) {
		return !item.isEmpty() && getAllItems().contains(item);
	}

	@Override
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

	@Override
	public String toString() {
		return ingredient.toString();
	}
}
