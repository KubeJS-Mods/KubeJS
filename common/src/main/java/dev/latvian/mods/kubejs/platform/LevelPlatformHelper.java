package dev.latvian.mods.kubejs.platform;

import dev.latvian.mods.kubejs.block.entity.BlockEntityInfo;
import dev.latvian.mods.kubejs.block.entity.BlockEntityJS;
import dev.latvian.mods.kubejs.core.InventoryKJS;
import dev.latvian.mods.kubejs.util.Lazy;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public interface LevelPlatformHelper {

	Lazy<LevelPlatformHelper> INSTANCE = Lazy.serviceLoader(LevelPlatformHelper.class);

	static LevelPlatformHelper get() {
		return INSTANCE.get();
	}

	@Nullable
	InventoryKJS getInventoryFromBlockEntity(BlockEntity tileEntity, Direction facing);

	boolean areCapsCompatible(ItemStack a, ItemStack b);

	double getReachDistance(LivingEntity livingEntity);

	BlockEntityJS createBlockEntity(BlockPos pos, BlockState state, BlockEntityInfo info);
}
