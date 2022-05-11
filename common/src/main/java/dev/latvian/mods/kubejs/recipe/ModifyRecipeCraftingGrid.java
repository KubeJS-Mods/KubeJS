package dev.latvian.mods.kubejs.recipe;

import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ModifyRecipeCraftingGrid {
	private final CraftingContainer container;

	public ModifyRecipeCraftingGrid(CraftingContainer c) {
		container = c;
	}

	public ItemStackJS get(int index) {
		return ItemStackJS.of(container.getItem(index));
	}

	public List<ItemStackJS> findAll(IngredientJS ingredient) {
		List<ItemStackJS> list = new ArrayList<>();

		for (int i = 0; i < container.getContainerSize(); i++) {
			ItemStack stack = container.getItem(i);

			if (!stack.isEmpty() && ingredient.testVanilla(stack)) {
				list.add(ItemStackJS.of(stack));
			}
		}

		return list;
	}

	public ItemStackJS find(IngredientJS ingredient, int skip) {
		for (int i = 0; i < container.getContainerSize(); i++) {
			ItemStack stack = container.getItem(i);

			if (!stack.isEmpty() && ingredient.testVanilla(stack)) {
				if (skip > 0) {
					skip--;
				} else {
					return ItemStackJS.of(stack);
				}
			}
		}

		return ItemStackJS.EMPTY;
	}

	public ItemStackJS find(IngredientJS ingredient) {
		return find(ingredient, 0);
	}

	public int getWidth() {
		return container.getWidth();
	}

	public int getHeight() {
		return container.getHeight();
	}
}
