package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.core.InventoryKJS;
import dev.latvian.mods.kubejs.level.LevelBlock;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ResourceHandler.class)
public interface ResourceHandlerItemMixin extends InventoryKJS {
	@Unique
	@SuppressWarnings("unchecked")
	default ResourceHandler<ItemResource> kjs$self() {
		return (ResourceHandler<ItemResource>) this;
	}

	@Override
	default boolean kjs$isMutable() {
		return false;
	}

	@Override
	default int kjs$getSlots() {
		return kjs$self().size();
	}

	@Override
	default ItemStack kjs$getStackInSlot(int slot) {
		var resource = kjs$self().getResource(slot);
		if (resource.isEmpty()) {
			return ItemStack.EMPTY;
		}
		return resource.toStack(kjs$self().getAmountAsInt(slot));
	}

	@Override
	default void kjs$setStackInSlot(int slot, ItemStack stack) {
		// No direct slot setting in the new API
		InventoryKJS.super.kjs$setStackInSlot(slot, stack);
	}

	@Override
	default ItemStack kjs$insertItem(int slot, ItemStack stack, boolean simulate) {
		if (stack.isEmpty()) {
			return stack;
		}
		var resource = ItemResource.of(stack);
		try (var tx = simulate ? Transaction.openRoot() : Transaction.openRoot()) {
			int inserted = kjs$self().insert(slot, resource, stack.getCount(), tx);
			if (!simulate) {
				tx.commit();
			}
			int remaining = stack.getCount() - inserted;
			return remaining <= 0 ? ItemStack.EMPTY : stack.copyWithCount(remaining);
		}
	}

	@Override
	default ItemStack kjs$extractItem(int slot, int amount, boolean simulate) {
		if (amount <= 0) {
			return ItemStack.EMPTY;
		}
		var resource = kjs$self().getResource(slot);
		if (resource.isEmpty()) {
			return ItemStack.EMPTY;
		}
		try (var tx = Transaction.openRoot()) {
			int extracted = kjs$self().extract(slot, resource, amount, tx);
			if (!simulate) {
				tx.commit();
			}
			return extracted <= 0 ? ItemStack.EMPTY : resource.toStack(extracted);
		}
	}

	@Override
	default int kjs$getSlotLimit(int slot) {
		return kjs$self().getCapacityAsInt(slot, ItemResource.EMPTY);
	}

	@Override
	default boolean kjs$isItemValid(int slot, ItemStack stack) {
		if (stack.isEmpty()) {
			return false;
		}
		return kjs$self().isValid(slot, ItemResource.of(stack));
	}

	@Override
	default @Nullable LevelBlock kjs$getBlock(Level level) {
		if (kjs$self() instanceof BlockEntity entity) {
			return level.kjs$getBlock(entity);
		}
		return null;
	}
}