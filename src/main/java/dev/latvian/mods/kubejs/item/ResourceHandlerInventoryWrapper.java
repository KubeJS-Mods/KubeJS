package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.core.InventoryKJS;
import dev.latvian.mods.kubejs.level.LevelBlock;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.Nullable;

public class ResourceHandlerInventoryWrapper implements InventoryKJS {
	private final ResourceHandler<ItemResource> handler;

	public ResourceHandlerInventoryWrapper(ResourceHandler<ItemResource> handler) {
		this.handler = handler;
	}

	@Override
	public boolean kjs$isMutable() {
		return false;
	}

	@Override
	public int kjs$getSlots() {
		return handler.size();
	}

	@Override
	public ItemStack kjs$getStackInSlot(int slot) {
		var resource = handler.getResource(slot);
		if (resource.isEmpty()) {
			return ItemStack.EMPTY;
		}
		return resource.toStack(handler.getAmountAsInt(slot));
	}

	@Override
	public ItemStack kjs$insertItem(int slot, ItemStack stack, boolean simulate) {
		if (stack.isEmpty()) {
			return stack;
		}
		var resource = ItemResource.of(stack);
		try (var tx = Transaction.openRoot()) {
			int inserted = handler.insert(slot, resource, stack.getCount(), tx);
			if (!simulate) {
				tx.commit();
			}
			int remaining = stack.getCount() - inserted;
			return remaining <= 0 ? ItemStack.EMPTY : stack.copyWithCount(remaining);
		}
	}

	@Override
	public ItemStack kjs$extractItem(int slot, int amount, boolean simulate) {
		if (amount <= 0) {
			return ItemStack.EMPTY;
		}
		var resource = handler.getResource(slot);
		if (resource.isEmpty()) {
			return ItemStack.EMPTY;
		}
		try (var tx = Transaction.openRoot()) {
			int extracted = handler.extract(slot, resource, amount, tx);
			if (!simulate) {
				tx.commit();
			}
			return extracted <= 0 ? ItemStack.EMPTY : resource.toStack(extracted);
		}
	}

	@Override
	public int kjs$getSlotLimit(int slot) {
		return handler.getCapacityAsInt(slot, ItemResource.EMPTY);
	}

	@Override
	public boolean kjs$isItemValid(int slot, ItemStack stack) {
		if (stack.isEmpty()) {
			return false;
		}
		return handler.isValid(slot, ItemResource.of(stack));
	}

	@Override
	@Nullable
	public LevelBlock kjs$getBlock(Level level) {
		if (handler instanceof BlockEntity entity) {
			return level.kjs$getBlock(entity);
		}
		return null;
	}
}
