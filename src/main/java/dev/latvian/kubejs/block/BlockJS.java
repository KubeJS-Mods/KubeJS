package dev.latvian.kubejs.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class BlockJS extends Block
{
	public final BlockBuilder properties;
	public VoxelShape shape;

	public BlockJS(BlockBuilder p)
	{
		super(p.createProperties());
		properties = p;
		shape = VoxelShapes.fullCube();

		if (!properties.customShape.isEmpty())
		{
			List<VoxelShape> s = new ArrayList<>(properties.customShape);
			shape = s.get(0);

			if (s.size() > 1)
			{
				s.remove(0);
				shape = VoxelShapes.or(shape, s.toArray(new VoxelShape[0]));
			}
		}

		if (properties.waterlogged)
		{
			setDefaultState(stateContainer.getBaseState().with(BlockStateProperties.WATERLOGGED, false));
		}
	}

	@Override
	@Deprecated
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
	{
		return shape;
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
	{
		if (BlockBuilder.current.waterlogged)
		{
			builder.add(BlockStateProperties.WATERLOGGED);
		}
	}

	@Override
	@Deprecated
	public FluidState getFluidState(BlockState state)
	{
		return properties.waterlogged && state.get(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context)
	{
		if (!properties.waterlogged)
		{
			return getDefaultState();
		}

		return getDefaultState().with(BlockStateProperties.WATERLOGGED, context.getWorld().getFluidState(context.getPos()).getFluid() == Fluids.WATER);
	}

	@Override
	@Deprecated
	public BlockState updatePostPlacement(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos pos, BlockPos facingPos)
	{
		if (properties.waterlogged && state.get(BlockStateProperties.WATERLOGGED))
		{
			world.getPendingFluidTicks().scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}

		return state;
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos)
	{
		return !(properties.waterlogged && state.get(BlockStateProperties.WATERLOGGED));
	}
}