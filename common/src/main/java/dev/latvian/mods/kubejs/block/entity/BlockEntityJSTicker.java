package dev.latvian.mods.kubejs.block.entity;

import dev.latvian.mods.kubejs.util.ConsoleJS;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;

public record BlockEntityJSTicker(BlockEntityInfo info, int frequency, int offset, BlockEntityCallback callback, boolean server) implements BlockEntityTicker<BlockEntityJS> {
	@Override
	public void tick(Level level, BlockPos blockPos, BlockState blockState, BlockEntityJS e) {
		if (frequency <= 1 || e.tick % frequency == offset) {
			try {
				callback.accept(e);
			} catch (Exception ex) {
				if (server) {
					ConsoleJS.SERVER.error("Error while ticking KubeJS block entity '" + info.blockBuilder.id + "'", ex, null);
				} else {
					ConsoleJS.CLIENT.error("Error while ticking KubeJS block entity '" + info.blockBuilder.id + "'", ex, null);
				}
			}

			e.postTick(true);
		} else {
			e.postTick(false);
		}
	}
}