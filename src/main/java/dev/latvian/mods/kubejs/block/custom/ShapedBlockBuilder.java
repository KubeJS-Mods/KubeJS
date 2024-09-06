package dev.latvian.mods.kubejs.block.custom;

import dev.latvian.mods.kubejs.block.BlockBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public abstract class ShapedBlockBuilder extends BlockBuilder {
	public ShapedBlockBuilder(ResourceLocation i, String... suffixes) {
		super(i);
		notSolid();
		property(BlockStateProperties.WATERLOGGED);
		texture("kubejs:block/unknown");

		for (var s : suffixes) {
			if (id.getPath().endsWith(s)) {
				texture(id.withPath("block/" + id.getPath().substring(0, id.getPath().length() - s.length())).toString());
				break;
			}
		}
	}
}
