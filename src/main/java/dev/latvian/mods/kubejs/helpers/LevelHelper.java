package dev.latvian.mods.kubejs.helpers;

import dev.latvian.mods.kubejs.block.entity.BlockEntityInfo;
import dev.latvian.mods.kubejs.block.entity.BlockEntityJS;
import dev.latvian.mods.kubejs.core.InventoryKJS;
import dev.latvian.mods.kubejs.level.BlockContainerJS;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import org.jetbrains.annotations.Nullable;

public enum LevelHelper {
	INSTANCE;

	public static LevelHelper get() {
		return INSTANCE;
	}

	@Nullable
	public InventoryKJS getInventoryFromBlockEntity(BlockContainerJS block, Direction facing) {
		var handler = block.getLevel().getCapability(Capabilities.ItemHandler.BLOCK, block.getPos(),
			block.getBlockState(), block.getEntity(), facing);

		if (handler instanceof InventoryKJS inv) {
			return inv;
		}

		return null;
	}

	public boolean isDataCompatible(ItemStack a, ItemStack b) {
		return ItemStack.isSameItemSameComponents(a, b);
	}

	public double getReachDistance(LivingEntity livingEntity) {
		return livingEntity.getAttribute(Attributes.BLOCK_INTERACTION_RANGE).getValue();
	}

	public BlockEntityJS createBlockEntity(BlockPos pos, BlockState state, BlockEntityInfo info) {
		return new BlockEntityJS(pos, state, info);
	}
}
