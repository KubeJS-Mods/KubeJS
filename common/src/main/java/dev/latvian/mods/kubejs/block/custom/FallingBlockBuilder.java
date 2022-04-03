package dev.latvian.mods.kubejs.block.custom;

import dev.latvian.mods.kubejs.block.BlockBuilder;
import dev.latvian.mods.rhino.mod.util.color.Color;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;

public class FallingBlockBuilder extends BlockBuilder {
	private int dustColor = -8356741;

	public FallingBlockBuilder(ResourceLocation i) {
		super(i);
	}

	@Override
	public Block createObject() {
		return new FallingBlock(createProperties()) {
			@Override
			public int getDustColor(BlockState state, BlockGetter level, BlockPos pos) {
				return dustColor;
			}
		};
	}

	public FallingBlockBuilder dustColor(Color color) {
		dustColor = color.getRgbKJS();
		return this;
	}
}
