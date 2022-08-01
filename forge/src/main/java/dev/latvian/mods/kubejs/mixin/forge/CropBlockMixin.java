package dev.latvian.mods.kubejs.mixin.forge;

import dev.latvian.mods.kubejs.block.custom.BasicCropBlockJS;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = BasicCropBlockJS.class, remap = false)
public abstract class CropBlockMixin implements IPlantable {
	@Override
	public PlantType getPlantType(BlockGetter level, BlockPos pos) {
		return PlantType.CROP;
	}

	@Override
	public BlockState getPlant(BlockGetter world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);
		Block thisBlock = (Block) ((Object) this);
		return state.getBlock() != thisBlock ? thisBlock.defaultBlockState() : state;
	}
}
