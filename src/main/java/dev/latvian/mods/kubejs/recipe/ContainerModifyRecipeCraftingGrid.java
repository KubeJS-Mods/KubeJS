package dev.latvian.mods.kubejs.recipe;

import dev.latvian.mods.kubejs.core.CraftingContainerKJS;
import dev.latvian.mods.kubejs.util.SlotFilter;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ContainerModifyRecipeCraftingGrid implements ModifyRecipeCraftingGrid {
	private final CraftingContainer container;

	public ContainerModifyRecipeCraftingGrid(CraftingContainer c) {
		container = c;
	}

	@Override
	public List<ItemStack> findAll(SlotFilter filter) {
		List<ItemStack> list = new ArrayList<>();

		for (int i = 0; i < container.getContainerSize(); i++) {
			ItemStack stack = container.getItem(i);

			if (filter.checkFilter(i, stack)) {
				list.add(stack.copy());
			}
		}

		return list;
	}

	@Override
	public int getWidth() {
		return container.getWidth();
	}

	@Override
	public int getHeight() {
		return container.getHeight();
	}

	@Override
	@Nullable
	public AbstractContainerMenu getMenu() {
		return ((CraftingContainerKJS) container).kjs$getMenu();
	}
}
