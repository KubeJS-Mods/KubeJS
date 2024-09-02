package dev.latvian.mods.kubejs.client;

import dev.latvian.mods.kubejs.block.BlockTintFunction;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public record BlockTintFunctionWrapper(BlockTintFunction function) implements BlockColor {
	@Override
	public int getColor(BlockState state, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos, int index) {
		var c = function.getColor(state, level, pos, index);
		return c == null ? 0xFFFFFFFF : c.kjs$getARGB();
	}
}
