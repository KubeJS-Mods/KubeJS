package dev.latvian.mods.kubejs.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;

public record BlockEntityJSTicker(int frequency, int offset, BlockEntityCallback callback) implements BlockEntityTicker<BlockEntityJS> {
	@Override
	public void tick(Level level, BlockPos blockPos, BlockState blockState, BlockEntityJS e) {
		if (frequency <= 1 || e.tick % frequency == offset) {
			callback.accept(e);
			e.postTick(true);
		} else {
			e.postTick(false);
		}
	}
}