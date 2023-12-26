package dev.latvian.mods.kubejs.core.mixin.forge;

import dev.latvian.mods.kubejs.core.InventoryKJS;
import dev.latvian.mods.kubejs.level.BlockContainerJS;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = IItemHandler.class, remap = false)
public interface IItemHandlerMixin extends InventoryKJS {
	default IItemHandler kjs$self() {
		return (IItemHandler) this;
	}

	@Override
	default boolean kjs$isMutable() {
		return kjs$self() instanceof IItemHandlerModifiable;
	}

	@Override
	default int kjs$getSlots() {
		return kjs$self().getSlots();
	}

	@Override
	default ItemStack kjs$getStackInSlot(int i) {
		return kjs$self().getStackInSlot(i);
	}

	@Override
	default void kjs$setStackInSlot(int slot, ItemStack stack) {
		if (kjs$self() instanceof IItemHandlerModifiable mod) {
			mod.setStackInSlot(slot, stack);
		} else {
			InventoryKJS.super.kjs$setStackInSlot(slot, stack);
		}
	}

	@Override
	default ItemStack kjs$insertItem(int i, ItemStack itemStack, boolean b) {
		return kjs$self().insertItem(i, itemStack, b);
	}

	@Override
	default ItemStack kjs$extractItem(int i, int i1, boolean b) {
		return kjs$self().extractItem(i, i1, b);
	}

	@Override
	default int kjs$getSlotLimit(int i) {
		return kjs$self().getSlotLimit(i);
	}

	@Override
	default boolean kjs$isItemValid(int i, ItemStack itemStack) {
		return kjs$self().isItemValid(i, itemStack);
	}

	@Override
	default @Nullable BlockContainerJS kjs$getBlock(Level level) {
		if (kjs$self() instanceof BlockEntity entity) {
			return level.kjs$getBlock(entity);
		}

		return null;
	}
}
