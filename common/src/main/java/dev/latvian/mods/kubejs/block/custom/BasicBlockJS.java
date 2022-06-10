package dev.latvian.mods.kubejs.block.custom;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.RegistryObjectBuilderTypes;
import dev.latvian.mods.kubejs.block.BlockBuilder;
import dev.latvian.mods.kubejs.block.EntityBlockKJS;
import dev.latvian.mods.kubejs.block.RandomTickCallbackJS;
import dev.latvian.mods.kubejs.level.BlockContainerJS;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

/**
 * @author LatvianModder
 */
public class BasicBlockJS extends Block implements EntityBlockKJS {
	public static class Builder extends BlockBuilder {
		public Builder(ResourceLocation i) {
			super(i);
		}

		@Override
		public Block createObject() {
			return new BasicBlockJS(this);
		}
	}

	public final BlockBuilder blockBuilder;
	public final VoxelShape shape;

	public BasicBlockJS(BlockBuilder p) {
		super(p.createProperties());
		blockBuilder = p;
		shape = p.createShape();

		if (blockBuilder.waterlogged) {
			registerDefaultState(stateDefinition.any().setValue(BlockStateProperties.WATERLOGGED, false));
		}
	}

	@Override
	public BlockBuilder getBlockBuilderKJS() {
		return blockBuilder;
	}

	@Override
	@Deprecated
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return shape;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		if (RegistryObjectBuilderTypes.BLOCK.getCurrent() instanceof BlockBuilder current) {
			if (current.waterlogged) {
				builder.add(BlockStateProperties.WATERLOGGED);
			}
		}
	}

	@Override
	@Deprecated
	public FluidState getFluidState(BlockState state) {
		return blockBuilder.waterlogged && state.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		if (!blockBuilder.waterlogged) {
			return defaultBlockState();
		}

		return defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
	}

	@Override
	@Deprecated
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos pos, BlockPos facingPos) {
		if (blockBuilder.waterlogged && state.getValue(BlockStateProperties.WATERLOGGED)) {
			world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
		}

		return state;
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
		return blockBuilder.transparent || !(blockBuilder.waterlogged && state.getValue(BlockStateProperties.WATERLOGGED));
	}

	@Override
	@Deprecated
	public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (blockBuilder.randomTickCallback != null) {
			var containerJS = new BlockContainerJS(level, pos);
			try {
				blockBuilder.randomTickCallback.accept(new RandomTickCallbackJS(containerJS, random));
			} catch (Exception e) {
				KubeJS.LOGGER.error("Error while random ticking custom block {}: {}", this, e);
			}
		}
	}

	@Override
	public boolean isRandomlyTicking(BlockState state) {
		return blockBuilder.randomTickCallback != null;
	}

	@Override
	@Deprecated
	public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
		return blockBuilder.transparent ? Shapes.empty() : super.getVisualShape(state, level, pos, ctx);
	}

	@Override
	@Deprecated
	public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
		return blockBuilder.transparent ? 1F : super.getShadeBrightness(state, level, pos);
	}

	@Override
	@Deprecated
	public boolean skipRendering(BlockState state, BlockState state2, Direction direction) {
		return blockBuilder.transparent ? (state2.is(this) || super.skipRendering(state, state2, direction)) : super.skipRendering(state, state2, direction);
	}
}