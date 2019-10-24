package dev.latvian.kubejs.block;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author LatvianModder
 */
public class BlockJS extends Block
{
	public final BlockBuilder properties = BlockBuilder.current;

	public BlockJS()
	{
		super(BlockBuilder.current.material.getMinecraftMaterial());
		setTranslationKey(properties.translationKey);
		setHardness(properties.hardness);

		if (properties.resistance >= 0F)
		{
			setResistance(properties.resistance);
		}

		setLightLevel(properties.lightLevel);

		if (properties.harvestTool != null)
		{
			setHarvestLevel(properties.harvestTool, properties.harvestLevel);
		}
	}

	@Override
	@Deprecated
	public boolean isOpaqueCube(IBlockState state)
	{
		return (properties == null ? BlockBuilder.current : properties).opaque;
	}

	@Override
	@Deprecated
	public boolean isFullBlock(IBlockState state)
	{
		return (properties == null ? BlockBuilder.current : properties).fullBlock;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getRenderLayer()
	{
		return properties.layer;
	}
}