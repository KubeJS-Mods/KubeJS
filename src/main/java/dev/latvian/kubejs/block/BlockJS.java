package dev.latvian.kubejs.block;

import net.minecraft.block.Block;

/**
 * @author LatvianModder
 */
public class BlockJS extends Block
{
	public final BlockBuilder properties;

	public BlockJS(BlockBuilder p)
	{
		super(p.material.material);
		properties = p;
		setTranslationKey(p.translationKey);
		setHardness(p.hardness);

		if (p.resistance >= 0F)
		{
			setResistance(p.resistance);
		}

		setLightLevel(p.lightLevel);

		if (p.harvestTool != null)
		{
			setHarvestLevel(p.harvestTool, p.harvestLevel);
		}
	}
}