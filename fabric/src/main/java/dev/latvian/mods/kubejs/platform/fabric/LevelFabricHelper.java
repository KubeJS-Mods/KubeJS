package dev.latvian.mods.kubejs.platform.fabric;

import dev.latvian.mods.kubejs.core.InventoryKJS;
import dev.latvian.mods.kubejs.platform.LevelPlatformHelper;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public class LevelFabricHelper implements LevelPlatformHelper {
	@Override
	@Nullable
	public InventoryKJS getInventoryFromBlockEntity(BlockEntity tileEntity, Direction facing) {
		return null;
	}

	@Override
	public boolean areCapsCompatible(ItemStack a, ItemStack b) {
		return true;
	}

	@Override
	public double getReachDistance(LivingEntity livingEntity) {
		return 5;
	}
}
