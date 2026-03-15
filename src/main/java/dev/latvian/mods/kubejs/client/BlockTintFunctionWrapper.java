package dev.latvian.mods.kubejs.client;

import dev.latvian.mods.kubejs.block.BlockTintFunction;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public record BlockTintFunctionWrapper(BlockTintFunction function, int layerIndex) implements BlockTintSource {

	@Override
	public int color(BlockState state) {
		var c = function.getColor(state, null, null, layerIndex);
		return c == null ? 0xFFFFFFFF : c.kjs$getARGB();
	}

	@Override
	public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
		var c = function.getColor(state, level, pos, layerIndex);
		return c == null ? 0xFFFFFFFF : c.kjs$getARGB();
	}
}
