package dev.latvian.mods.kubejs.client;

import dev.latvian.mods.kubejs.block.BlockTintFunction;
import dev.latvian.mods.kubejs.color.KubeColor;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.client.fluid.FluidTintSource;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jspecify.annotations.Nullable;

public record FluidTintFunctionWrapper(BlockTintFunction function, int layerIndex, @Nullable KubeColor bucketColor) implements FluidTintSource {

	@Override
	public int color(FluidState state) {
		return color(state.createLegacyBlock());
	}

	@Override
	public int colorInWorld(FluidState fluidState, BlockState state, BlockAndTintGetter level, BlockPos pos) {
		var c = function.getColor(state, level, pos, layerIndex);
		return c == null ? 0xFFFFFFFF : c.kjs$getARGB();
	}

	@Override
	public int colorAsStack(FluidStack stack) {
		if (bucketColor != null) {
			return bucketColor.kjs$getARGB();
		}
		if (function instanceof BlockTintFunction.Fixed(KubeColor color)) {
			return color.kjs$getARGB();
		}
		return 0xFFFFFFFF;
	}

	@Override
	public int color(BlockState state) {
		if (function instanceof BlockTintFunction.Fixed(KubeColor color)) {
			return color.kjs$getARGB();
		}
		return 0xFFFFFFFF;
	}

	@Override
	public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
		var c = function.getColor(state, level, pos, layerIndex);
		return c == null ? 0xFFFFFFFF : c.kjs$getARGB();
	}
}
