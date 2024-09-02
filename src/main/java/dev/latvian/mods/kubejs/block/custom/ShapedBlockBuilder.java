package dev.latvian.mods.kubejs.block.custom;

import dev.latvian.mods.kubejs.block.BlockBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public abstract class ShapedBlockBuilder extends BlockBuilder {
	public ShapedBlockBuilder(ResourceLocation i, String... suffixes) {
		super(i);
		notSolid();
		property(BlockStateProperties.WATERLOGGED);
		texture("texture", "kubejs:block/detector");

		for (var s : suffixes) {
			if (id.getPath().endsWith(s)) {
				texture("texture", id.withPath("block/" + id.getPath().substring(0, id.getPath().length() - s.length())).toString());
				break;
			}
		}
	}

	@Override
	public BlockBuilder textureAll(String tex) {
		super.textureAll(tex);
		return texture("texture", tex);
	}
}
