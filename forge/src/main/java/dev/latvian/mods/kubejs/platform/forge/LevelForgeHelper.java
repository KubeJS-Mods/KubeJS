package dev.latvian.mods.kubejs.platform.forge;

import dev.latvian.mods.kubejs.block.entity.BlockEntityInfo;
import dev.latvian.mods.kubejs.block.entity.BlockEntityJS;
import dev.latvian.mods.kubejs.core.InventoryKJS;
import dev.latvian.mods.kubejs.level.BlockContainerJS;
import dev.latvian.mods.kubejs.platform.LevelPlatformHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.NeoForgeMod;
import org.jetbrains.annotations.Nullable;

public class LevelForgeHelper implements LevelPlatformHelper {
	@Override
	@Nullable
	public InventoryKJS getInventoryFromBlockEntity(BlockContainerJS block, Direction facing) {
		var handler = block.getLevel().getCapability(Capabilities.ItemHandler.BLOCK, block.getPos(),
			block.getBlockState(), block.getEntity(), facing);

		if (handler instanceof InventoryKJS inv) {
			return inv;
		}

		return null;
	}

	@Override
	public boolean isDataCompatible(ItemStack a, ItemStack b) {
		return a.areAttachmentsCompatible(b);
	}

	@Override
	public double getReachDistance(LivingEntity livingEntity) {
		return livingEntity.getAttribute(NeoForgeMod.ENTITY_REACH.value()).getValue();
	}

	@Override
	public BlockEntityJS createBlockEntity(BlockPos pos, BlockState state, BlockEntityInfo info) {
		return new BlockEntityJS(pos, state, info);
	}
}
