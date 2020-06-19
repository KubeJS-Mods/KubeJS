package dev.latvian.kubejs.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

import java.util.HashMap;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class BlockJS extends Block
{
	public static final Map<ResourceLocation, BlockJS> KUBEJS_BLOCKS = new HashMap<>();

	public final BlockBuilder properties;
	public VoxelShape shape;

	public BlockJS(BlockBuilder p)
	{
		super(p.createProperties());
		properties = p;
		shape = VoxelShapes.fullCube();

		if (!properties.customShape.isEmpty())
		{
			shape = properties.customShape.get(0);

			if (properties.customShape.size() > 1)
			{
				properties.customShape.remove(0);
				shape = VoxelShapes.or(shape, properties.customShape.toArray(new VoxelShape[0]));
			}
		}
	}

	@Override
	@Deprecated
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
	{
		return shape;
	}
}