package dev.latvian.mods.kubejs.recipe;

import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.ItemStackSet;
import dev.latvian.mods.kubejs.item.OutputItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

public class IngredientMatch implements ReplacementMatch {
	public final Ingredient ingredient;
	public final boolean exact;
	private ItemStackSet allItems;
	private ItemStack[] allItemsArray;

	public IngredientMatch(Ingredient ingredient, boolean exact) {
		this.ingredient = ingredient;
		this.exact = exact;
	}

	@Override
	public String toString() {
		return ingredient.toString();
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

	public boolean contains(Block block) {
		var item = block.asItem();
		return item != Items.AIR && contains(new ItemStack(item));
	}
}
