package dev.latvian.kubejs.block;

import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class BlockJS extends Block
{
	public static final Map<ResourceLocation, BlockJS> KUBEJS_BLOCKS = new HashMap<>();

	public final BlockBuilder properties;

	public BlockJS(BlockBuilder p)
	{
		super(p.createProperties());
		properties = p;
	}
}