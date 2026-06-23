package dev.latvian.mods.kubejs.client;

import dev.latvian.mods.kubejs.block.BlockTintFunction;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public record BlockTintFunctionWrapper(BlockTintFunction function, int layerIndex) implements BlockTintSource {
	public static int getAverageGrassColor(BlockGetter level, BlockPos pos) {
		return BiomeColors.getAverageGrassColor((BlockAndTintGetter) level, pos);
	}

	public static int getAverageFoliageColor(BlockGetter level, BlockPos pos) {
		return BiomeColors.getAverageFoliageColor((BlockAndTintGetter) level, pos);
	}

	public static int getAverageWaterColor(BlockGetter level, BlockPos pos) {
		return BiomeColors.getAverageWaterColor((BlockAndTintGetter) level, pos);
	}

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
