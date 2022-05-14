package dev.latvian.mods.kubejs.block.custom;

import dev.latvian.mods.kubejs.block.BlockBuilder;
import net.minecraft.resources.ResourceLocation;

public abstract class ShapedBlockBuilder extends BlockBuilder {
	public ShapedBlockBuilder(ResourceLocation i, String... suffixes) {
		super(i);
		notSolid();
		waterlogged();
		texture("texture", "kubejs:block/detector");

		for (String s : suffixes) {
			if (id.getPath().endsWith(s)) {
				texture("texture", id.getNamespace() + ":block/" + id.getPath().substring(0, id.getPath().length() - s.length()));
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
