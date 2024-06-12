package dev.latvian.mods.kubejs.recipe;

import dev.latvian.mods.kubejs.util.SlotFilter;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ModifyRecipeCraftingGrid {
	List<ItemStack> findAll(SlotFilter filter);

	default List<ItemStack> findAll() {
		return findAll(SlotFilter.EMPTY);
	}

	default ItemStack find(SlotFilter filter, int skip) {
		for (var item : findAll(filter)) {
			if (skip == 0) {
				return item;
			}

			skip--;
		}

		return ItemStack.EMPTY;
	}

	default ItemStack find(SlotFilter filter) {
		return find(filter, 0);
	}

	default int getWidth() {
		return 0;
	}

	default int getHeight() {
		return 0;
	}

	@Nullable
	default AbstractContainerMenu getMenu() {
		return null;
	}
}
