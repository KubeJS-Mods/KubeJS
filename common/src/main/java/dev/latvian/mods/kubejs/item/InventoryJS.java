package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.level.BlockContainerJS;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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

	public ItemStack get(int slot) {
		return minecraftInventory.getStackInSlot(slot);
	}

	public void set(int slot, ItemStack item) {
		if (minecraftInventory instanceof ItemHandler.Mutable m) {
			m.setStackInSlot(slot, item);
		} else {
			throw new IllegalStateException("This inventory can't be modified directly! Use insert/extract methods!");
		}
	}

	public ItemStack insert(int slot, ItemStack item, boolean simulate) {
		return minecraftInventory.insertItem(slot, item, simulate);
	}

	public ItemStack extract(int slot, int amount, boolean simulate) {
		return minecraftInventory.extractItem(slot, amount, simulate);
	}

	public int getSlotLimit(int slot) {
		return minecraftInventory.getSlotLimit(slot);
	}

	public boolean isItemValid(int slot, ItemStack item) {
		return minecraftInventory.isItemValid(slot, item);
	}

	public void clear() {
		var modInv = minecraftInventory instanceof ItemHandler.Mutable m ? m : null;

		for (var i = minecraftInventory.getSlots(); i >= 0; i--) {
			if (modInv != null) {
				modInv.setStackInSlot(i, ItemStack.EMPTY);
			} else {
				minecraftInventory.extractItem(i, minecraftInventory.getStackInSlot(i).getCount(), false);
			}
		}
	}

	public void clear(Ingredient ingredient) {
		if (ingredient.kjs$isWildcard()) {
			clear();
		}

		var modInv = minecraftInventory instanceof ItemHandler.Mutable m ? m : null;

		for (var i = minecraftInventory.getSlots(); i >= 0; i--) {
			if (ingredient.test(minecraftInventory.getStackInSlot(i))) {
				if (modInv != null) {
					modInv.setStackInSlot(i, ItemStack.EMPTY);
				} else {
					minecraftInventory.extractItem(i, minecraftInventory.getStackInSlot(i).getCount(), false);
				}
			}
		}
	}

	public int find() {
		for (var i = 0; i < minecraftInventory.getSlots(); i++) {
			var stack1 = minecraftInventory.getStackInSlot(i);

			if (!stack1.isEmpty()) {
				return i;
			}
		}

		return -1;
	}

	public int find(Ingredient ingredient) {
		if (ingredient.kjs$isWildcard()) {
			return find();
		}

		for (var i = 0; i < minecraftInventory.getSlots(); i++) {
			var stack1 = minecraftInventory.getStackInSlot(i);

			if (ingredient.test(stack1)) {
				return i;
			}
		}

		return -1;
	}

	public int count() {
		var count = 0;

		for (var i = 0; i < minecraftInventory.getSlots(); i++) {
			count += minecraftInventory.getStackInSlot(i).getCount();
		}

		return count;
	}

	public int count(Ingredient ingredient) {
		if (ingredient.kjs$isWildcard()) {
			return count();
		}

		var count = 0;

		for (var i = 0; i < minecraftInventory.getSlots(); i++) {
			var stack1 = minecraftInventory.getStackInSlot(i);

			if (ingredient.test(stack1)) {
				count += stack1.getCount();
			}
		}

		return count;
	}

	public int countNonEmpty() {
		var count = 0;

		for (var i = 0; i < minecraftInventory.getSlots(); i++) {
			if (!minecraftInventory.getStackInSlot(i).isEmpty()) {
				count++;
			}
		}

		return count;
	}

	public int countNonEmpty(Ingredient ingredient) {
		if (ingredient.kjs$isWildcard()) {
			return countNonEmpty();
		}

		var count = 0;

		for (var i = 0; i < minecraftInventory.getSlots(); i++) {
			var stack1 = minecraftInventory.getStackInSlot(i);

			if (ingredient.test(stack1)) {
				count++;
			}
		}

		return count;
	}

	public boolean isEmpty() {
		for (var i = 0; i < minecraftInventory.getSlots(); i++) {
			if (!minecraftInventory.getStackInSlot(i).isEmpty()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public String toString() {
		var list = new LinkedList<String>();

		for (var i = 0; i < getSize(); i++) {
			list.add(get(i).toString());
		}

		return list.toString();
	}

	public void markDirty() {
		if (minecraftInventory instanceof ContainerInventory container) {
			container.getInv().setChanged();
		}
	}

	@Nullable
	public BlockContainerJS getBlock(Level level) {
		if (minecraftInventory instanceof ContainerInventory container) {
			var inv = container.getInv();

			if (inv instanceof BlockEntity be) {
				return level.kjs$getBlock(be);
			}
		}

		return null;
	}

	public int getWidth() {
		return minecraftInventory.getWidth();
	}

	public int getHeight() {
		return minecraftInventory.getHeight();
	}

	public List<ItemStack> getAllItems() {
		var list = new ArrayList<ItemStack>();

		for (var i = 0; i < minecraftInventory.getSlots(); i++) {
			ItemStack is = minecraftInventory.getStackInSlot(i);

			if (!is.isEmpty()) {
				list.add(is);
			}
		}

		return list;
	}
}