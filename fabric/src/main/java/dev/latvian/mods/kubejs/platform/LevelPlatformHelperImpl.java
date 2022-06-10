package dev.latvian.mods.kubejs.platform;

import dev.latvian.mods.kubejs.item.InventoryJS;
import dev.latvian.mods.kubejs.level.LevelPlatformHelper;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class LevelPlatformHelperImpl implements LevelPlatformHelper {
	public InventoryJS getInventoryFromBlockEntity(BlockEntity tileEntity, Direction facing) {
		return null;
	}

	public boolean areCapsCompatible(ItemStack a, ItemStack b) {
		return true;
	}

	public ItemStack getContainerItem(ItemStack stack) {
		var item = stack.getItem();

		if (item.hasCraftingRemainingItem()) {
			return new ItemStack(item.getCraftingRemainingItem());
		}

		return ItemStack.EMPTY;
	}

	public double getReachDistance(LivingEntity livingEntity) {
		return 5;
	}
}
