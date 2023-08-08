package dev.latvian.mods.kubejs.recipe;

import dev.latvian.mods.kubejs.core.CraftingContainerKJS;
import dev.latvian.mods.kubejs.platform.IngredientPlatformHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ModifyRecipeCraftingGrid {
	private final CraftingContainer container;

	public ModifyRecipeCraftingGrid(CraftingContainer c) {
		container = c;
	}

	public ItemStack get(int index) {
		return container.getItem(index).copy();
	}

	public List<ItemStack> findAll(Ingredient ingredient) {
		List<ItemStack> list = new ArrayList<>();

		for (int i = 0; i < container.getContainerSize(); i++) {
			ItemStack stack = container.getItem(i);

			if (ingredient.test(stack)) {
				list.add(stack.copy());
			}
		}

		return list;
	}

	public List<ItemStack> findAll() {
		return findAll(IngredientPlatformHelper.get().wildcard());
	}

	public ItemStack find(Ingredient ingredient, int skip) {
		for (int i = 0; i < container.getContainerSize(); i++) {
			ItemStack stack = container.getItem(i);

			if (ingredient.test(stack)) {
				if (skip > 0) {
					skip--;
				} else {
					return stack.copy();
				}
			}
		}

		return ItemStack.EMPTY;
	}

	public ItemStack find(Ingredient ingredient) {
		return find(ingredient, 0);
	}

	public int getWidth() {
		return container.getWidth();
	}

	public int getHeight() {
		return container.getHeight();
	}

	@Nullable
	public AbstractContainerMenu getMenu() {
		return ((CraftingContainerKJS) container).kjs$getMenu();
	}

	@Nullable
	public Player getPlayer() {
		if (getMenu() instanceof CraftingMenu menu && menu.player != null) {
			return menu.player;
		} else if (getMenu() instanceof InventoryMenu menu && menu.owner != null) {
			return menu.owner;
		}

		return null;
	}
}
