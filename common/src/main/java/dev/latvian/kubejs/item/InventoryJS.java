package dev.latvian.kubejs.item;

import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.item.ingredient.MatchAllIngredientJS;
import dev.latvian.kubejs.world.BlockContainerJS;
import dev.latvian.kubejs.world.WorldJS;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;

/**
 * @author LatvianModder
 */
public class InventoryJS {
	public final ItemHandler minecraftInventory;

	public InventoryJS(ItemHandler h) {
		minecraftInventory = h;
	}

	public InventoryJS(Container h) {
		minecraftInventory = new ContainerInventory(h);
	}

	public int getSize() {
		return minecraftInventory.getSlots();
	}

	public ItemStackJS get(int slot) {
		return ItemStackJS.of(minecraftInventory.getStackInSlot(slot));
	}

	public void set(int slot, Object item) {
		if (minecraftInventory instanceof ItemHandler.Mutable) {
			((ItemHandler.Mutable) minecraftInventory).setStackInSlot(slot, ItemStackJS.of(item).getItemStack());
		} else {
			throw new IllegalStateException("This inventory can't be modified directly! Use insert/extract methods!");
		}
	}

	public ItemStackJS insert(int slot, Object item, boolean simulate) {
		return ItemStackJS.of(minecraftInventory.insertItem(slot, ItemStackJS.of(item).getItemStack(), simulate));
	}

	public ItemStackJS extract(int slot, int amount, boolean simulate) {
		return ItemStackJS.of(minecraftInventory.extractItem(slot, amount, simulate));
	}

	public int getSlotLimit(int slot) {
		return minecraftInventory.getSlotLimit(slot);
	}

	public boolean isItemValid(int slot, Object item) {
		return minecraftInventory.isItemValid(slot, ItemStackJS.of(item).getItemStack());
	}

	public void clear() {
		ItemHandler.Mutable modInv = minecraftInventory instanceof ItemHandler.Mutable ? (ItemHandler.Mutable) minecraftInventory : null;

		for (int i = minecraftInventory.getSlots(); i >= 0; i--) {
			if (modInv != null) {
				modInv.setStackInSlot(i, ItemStack.EMPTY);
			} else {
				minecraftInventory.extractItem(i, minecraftInventory.getStackInSlot(i).getCount(), false);
			}
		}
	}

	public void clear(Object o) {
		IngredientJS ingredient = IngredientJS.of(o);

		if (ingredient == MatchAllIngredientJS.INSTANCE) {
			clear();
		}

		ItemHandler.Mutable modInv = minecraftInventory instanceof ItemHandler.Mutable ? (ItemHandler.Mutable) minecraftInventory : null;

		for (int i = minecraftInventory.getSlots(); i >= 0; i--) {
			if (ingredient.testVanilla(minecraftInventory.getStackInSlot(i))) {
				if (modInv != null) {
					modInv.setStackInSlot(i, ItemStack.EMPTY);
				} else {
					minecraftInventory.extractItem(i, minecraftInventory.getStackInSlot(i).getCount(), false);
				}
			}
		}
	}

	public int find() {
		for (int i = 0; i < minecraftInventory.getSlots(); i++) {
			ItemStack stack1 = minecraftInventory.getStackInSlot(i);

			if (!stack1.isEmpty()) {
				return i;
			}
		}

		return -1;
	}

	public int find(Object o) {
		IngredientJS ingredient = IngredientJS.of(o);

		if (ingredient == MatchAllIngredientJS.INSTANCE) {
			return find();
		}

		for (int i = 0; i < minecraftInventory.getSlots(); i++) {
			ItemStack stack1 = minecraftInventory.getStackInSlot(i);

			if (ingredient.testVanilla(stack1)) {
				return i;
			}
		}

		return -1;
	}

	public int count() {
		int count = 0;

		for (int i = 0; i < minecraftInventory.getSlots(); i++) {
			count += minecraftInventory.getStackInSlot(i).getCount();
		}

		return count;
	}

	public int count(Object o) {
		IngredientJS ingredient = IngredientJS.of(o);

		if (ingredient == MatchAllIngredientJS.INSTANCE) {
			return count();
		}

		int count = 0;

		for (int i = 0; i < minecraftInventory.getSlots(); i++) {
			ItemStack stack1 = minecraftInventory.getStackInSlot(i);

			if (ingredient.testVanilla(stack1)) {
				count += stack1.getCount();
			}
		}

		return count;
	}

	public int countNonEmpty() {
		int count = 0;

		for (int i = 0; i < minecraftInventory.getSlots(); i++) {
			if (!minecraftInventory.getStackInSlot(i).isEmpty()) {
				count++;
			}
		}

		return count;
	}

	public int countNonEmpty(Object o) {
		IngredientJS ingredient = IngredientJS.of(o);

		if (ingredient == MatchAllIngredientJS.INSTANCE) {
			return countNonEmpty();
		}

		int count = 0;

		for (int i = 0; i < minecraftInventory.getSlots(); i++) {
			ItemStack stack1 = minecraftInventory.getStackInSlot(i);

			if (ingredient.testVanilla(stack1)) {
				count++;
			}
		}

		return count;
	}

	public boolean isEmpty() {
		for (int i = 0; i < minecraftInventory.getSlots(); i++) {
			if (!minecraftInventory.getStackInSlot(i).isEmpty()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public String toString() {
		LinkedList<String> list = new LinkedList<>();

		for (int i = 0; i < getSize(); i++) {
			list.add(get(i).toString());
		}

		return list.toString();
	}

	public void markDirty() {
		if (minecraftInventory instanceof ContainerInventory) {
			((ContainerInventory) minecraftInventory).getInv().setChanged();
		}
	}

	@Nullable
	public BlockContainerJS getBlock(WorldJS world) {
		if (minecraftInventory instanceof ContainerInventory) {
			Container inv = ((ContainerInventory) minecraftInventory).getInv();

			if (inv instanceof BlockEntity) {
				return world.getBlock((BlockEntity) inv);
			}
		}

		return null;
	}
}