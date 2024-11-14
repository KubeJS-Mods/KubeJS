package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.core.InventoryKJS;
import dev.latvian.mods.kubejs.level.LevelBlock;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(IItemHandler.class)
public interface IItemHandlerMixin extends InventoryKJS {
	@Unique
	default IItemHandler kjs$self() {
		return (IItemHandler) this;
	}

	@Override
	default boolean kjs$isMutable() {
		return kjs$self() instanceof IItemHandlerModifiable;
	}

	@Override
	@Invoker("getSlots")
	int kjs$getSlots();

	@Override
	@Invoker("getStackInSlot")
	ItemStack kjs$getStackInSlot(int i);

	@Override
	default void kjs$setStackInSlot(int slot, ItemStack stack) {
		if (kjs$self() instanceof IItemHandlerModifiable mod) {
			mod.setStackInSlot(slot, stack);
		} else {
			InventoryKJS.super.kjs$setStackInSlot(slot, stack);
		}
	}

	@Override
	@Invoker("insertItem")
	ItemStack kjs$insertItem(int i, ItemStack itemStack, boolean b);

	@Override
	@Invoker("extractItem")
	ItemStack kjs$extractItem(int i, int i1, boolean b);

	@Override
	@Invoker("getSlotLimit")
	int kjs$getSlotLimit(int i);

	@Override
	@Invoker("isItemValid")
	boolean kjs$isItemValid(int i, ItemStack itemStack);

	@Override
	default @Nullable LevelBlock kjs$getBlock(Level level) {
		if (kjs$self() instanceof BlockEntity entity) {
			return level.kjs$getBlock(entity);
		}

		return null;
	}
}
