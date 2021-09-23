package dev.latvian.kubejs.block.custom;

import dev.latvian.kubejs.block.BlockBuilder;
import net.minecraft.world.level.block.Block;

public class BasicBlockType extends BlockType {
	public static final BasicBlockType INSTANCE = new BasicBlockType("basic");

	public BasicBlockType(String n) {
		super(n);
	}

	@Override
	public Block createBlock(BlockBuilder builder) {
		return new BasicBlockJS(builder);
	}
}
