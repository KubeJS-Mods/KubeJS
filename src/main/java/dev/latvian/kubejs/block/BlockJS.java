package dev.latvian.kubejs.block;

import net.minecraft.block.Block;
import net.minecraft.util.BlockRenderLayer;

/**
 * @author LatvianModder
 */
public class BlockJS extends Block
{
	public final BlockBuilder properties;

	public BlockJS(BlockBuilder p)
	{
		super(p.createProperties());
		properties = p;
	}

	@Override
	public BlockRenderLayer getRenderLayer()
	{
		return properties.layer;
	}
}