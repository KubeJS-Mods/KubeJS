package dev.latvian.mods.kubejs.platform.fabric;

import dev.latvian.mods.kubejs.block.entity.BlockEntityInfo;
import dev.latvian.mods.kubejs.block.entity.BlockEntityJS;
import dev.latvian.mods.kubejs.core.InventoryKJS;
import dev.latvian.mods.kubejs.platform.LevelPlatformHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
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
		if (livingEntity instanceof Player player){
			if (!player.isCreative()) return 4.5;
		}
		return 5;
	}

	@Override
	public BlockEntityJS createBlockEntity(BlockPos pos, BlockState state, BlockEntityInfo info) {
		return new BlockEntityJS(pos, state, info);
	}
}
