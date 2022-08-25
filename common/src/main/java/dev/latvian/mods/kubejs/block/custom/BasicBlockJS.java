package dev.latvian.mods.kubejs.block.custom;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.RegistryObjectBuilderTypes;
import dev.latvian.mods.kubejs.block.BlockBuilder;
import dev.latvian.mods.kubejs.block.BlockStateModifyCallbackJS;
import dev.latvian.mods.kubejs.block.BlockStateModifyPlacementCallbackJS;
import dev.latvian.mods.kubejs.block.EntityBlockKJS;
import dev.latvian.mods.kubejs.block.RandomTickCallbackJS;
import dev.latvian.mods.kubejs.level.BlockContainerJS;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class BasicBlockJS extends Block implements EntityBlockKJS, SimpleWaterloggedBlock {
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

		var blockState = stateDefinition.any();
		if(blockBuilder.defaultStateModification != null) {
			var callbackJS = new BlockStateModifyCallbackJS(blockState);
			if (safeCallback(blockBuilder.defaultStateModification, callbackJS, "Error while creating default blockState for block " + p.id)) {
				registerDefaultState(callbackJS.getState());
			}
		} else if(blockBuilder.blockStateProperties.contains(BlockStateProperties.WATERLOGGED)){
			registerDefaultState(blockState.setValue(BlockStateProperties.WATERLOGGED, false));
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
			for(Property<?> property : current.blockStateProperties) {
				builder.add(property);
			}
			current.blockStateProperties = Collections.unmodifiableSet(current.blockStateProperties);
		}
	}

	@Override
	@Deprecated
	public FluidState getFluidState(BlockState state) {
		return state.getOptionalValue(BlockStateProperties.WATERLOGGED).orElse(false) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		if(blockBuilder.placementStateModification != null) {
			var callbackJS = new BlockStateModifyPlacementCallbackJS(context, this);
			if (safeCallback(blockBuilder.placementStateModification, callbackJS, "Error while modifying BlockState placement of " + blockBuilder.id)) {
				return callbackJS.getState();
			}
		}
		if (!blockBuilder.blockStateProperties.contains(BlockStateProperties.WATERLOGGED)) {
			return defaultBlockState();
		}

		return defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
	}

	@Override
	@Deprecated
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos pos, BlockPos facingPos) {
		if (state.getOptionalValue(BlockStateProperties.WATERLOGGED).orElse(false)) {
			world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
		}

		return state;
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
		return blockBuilder.transparent || !(state.getOptionalValue(BlockStateProperties.WATERLOGGED).orElse(false));
	}

	@Override
	@Deprecated
	public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
		if (blockBuilder.randomTickCallback != null) {
			var callback = new RandomTickCallbackJS(new BlockContainerJS(level, pos), random);
			safeCallback(blockBuilder.randomTickCallback, callback, "Error while random ticking custom block "+this);
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

	@Nullable
	private <T> boolean safeCallback(Consumer<T> consumer, T value, String errorMessage) {
		try {
			consumer.accept(value);
		} catch(Throwable e) {
			KubeJS.startupScriptManager.type.console.error(errorMessage, e);
			return false;
		}

		return true;
	}

	@Override
	public boolean canPlaceLiquid(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState, Fluid fluid) {
		if(blockBuilder.blockStateProperties.contains(BlockStateProperties.WATERLOGGED))
			return SimpleWaterloggedBlock.super.canPlaceLiquid(blockGetter, blockPos, blockState, fluid);
		return false;
	}

	@Override
	public boolean placeLiquid(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState, FluidState fluidState) {
		if(blockBuilder.blockStateProperties.contains(BlockStateProperties.WATERLOGGED))
			return SimpleWaterloggedBlock.super.placeLiquid(levelAccessor, blockPos, blockState, fluidState);
		return false;
	}

	@Override
	public ItemStack pickupBlock(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState) {
		if(blockBuilder.blockStateProperties.contains(BlockStateProperties.WATERLOGGED))
			return SimpleWaterloggedBlock.super.pickupBlock(levelAccessor, blockPos, blockState);
		return ItemStack.EMPTY;
	}

	@Override
	public Optional<SoundEvent> getPickupSound() {
		if(blockBuilder.blockStateProperties.contains(BlockStateProperties.WATERLOGGED))
			return SimpleWaterloggedBlock.super.getPickupSound();
		return Optional.empty();
	}
}