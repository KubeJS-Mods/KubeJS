package dev.latvian.mods.kubejs.client;

import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;

public final class ClientBlockTintColors {
	private ClientBlockTintColors() {
	}

	public static int getAverageGrassColor(Object level, BlockPos pos) {
		return BiomeColors.getAverageGrassColor((BlockAndTintGetter) level, pos);
	}

	public static int getAverageFoliageColor(Object level, BlockPos pos) {
		return BiomeColors.getAverageFoliageColor((BlockAndTintGetter) level, pos);
	}

	public static int getAverageWaterColor(Object level, BlockPos pos) {
		return BiomeColors.getAverageWaterColor((BlockAndTintGetter) level, pos);
	}
}
