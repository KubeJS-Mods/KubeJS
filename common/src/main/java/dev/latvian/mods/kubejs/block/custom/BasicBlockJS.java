package dev.latvian.mods.kubejs.block.custom;

import com.mojang.serialization.MapCodec;
import dev.latvian.mods.kubejs.block.BlockBuilder;
import dev.latvian.mods.kubejs.block.BlockRightClickedEventJS;
import dev.latvian.mods.kubejs.block.KubeJSBlockProperties;
import dev.latvian.mods.kubejs.block.PickBlockCallbackJS;
import dev.latvian.mods.kubejs.block.RandomTickCallbackJS;
import dev.latvian.mods.kubejs.block.callbacks.AfterEntityFallenOnBlockCallbackJS;
import dev.latvian.mods.kubejs.block.callbacks.BlockExplodedCallbackJS;
import dev.latvian.mods.kubejs.block.callbacks.BlockStateMirrorCallbackJS;
import dev.latvian.mods.kubejs.block.callbacks.BlockStateModifyCallbackJS;
import dev.latvian.mods.kubejs.block.callbacks.BlockStateModifyPlacementCallbackJS;
import dev.latvian.mods.kubejs.block.callbacks.BlockStateRotateCallbackJS;
import dev.latvian.mods.kubejs.block.callbacks.CanBeReplacedCallbackJS;
import dev.latvian.mods.kubejs.block.callbacks.EntityFallenOnBlockCallbackJS;
import dev.latvian.mods.kubejs.block.callbacks.EntitySteppedOnBlockCallbackJS;
import dev.latvian.mods.kubejs.block.entity.BlockEntityJS;
import dev.latvian.mods.kubejs.core.BlockKJS;
import dev.latvian.mods.kubejs.level.BlockContainerJS;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class BasicBlockJS extends Block implements BlockKJS, SimpleWaterloggedBlock {
	public static class Builder extends BlockBuilder {
		public Builder(ResourceLocation i) {
			super(i);
		}

		@Override
		public Block createObject() {
			return blockEntityInfo != null ? new WithEntity(this) : new BasicBlockJS(this);
		}
	}

	public static class WithEntity extends BasicBlockJS implements EntityBlock {
		public WithEntity(BlockBuilder p) {
			super(p);
		}

		@Nullable
		@Override
		public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
			return blockBuilder.blockEntityInfo.createBlockEntity(blockPos, blockState);
		}

		@Nullable
		@Override
		public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
			return blockBuilder.blockEntityInfo.getTicker(level);
		}
	}

	public final BlockBuilder blockBuilder;
	public final VoxelShape shape;
	public Map<Map<String, Object>, VoxelShape> shapeMap = new HashMap<>();

	public BasicBlockJS(BlockBuilder p) {
		super(p.createProperties());
		blockBuilder = p;
		shape = BlockBuilder.createShape(p.customShape);


		var blockState = stateDefinition.any();
		this.shapeMap = p.getShapeMap(blockState.getProperties());
		if (blockBuilder.defaultStateModification != null) {
			var callbackJS = new BlockStateModifyCallbackJS(blockState);
			if (safeCallback(blockBuilder.defaultStateModification, callbackJS, "Error while creating default blockState for block " + p.id)) {
				registerDefaultState(callbackJS.getState());
			}
		} else if (blockBuilder.canBeWaterlogged()) {
			registerDefaultState(blockState.setValue(BlockStateProperties.WATERLOGGED, false));
		}
	}

	@Override
	public BlockBuilder kjs$getBlockBuilder() {
		return blockBuilder;
	}

	@Override
	public MutableComponent getName() {
		if (blockBuilder.displayName != null && blockBuilder.formattedDisplayName) {
			return Component.literal("").append(blockBuilder.displayName);
		}

		return super.getName();
	}

	@Override
	@Deprecated
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		Map<String, Object> blockPropertyValues = new HashMap<>();
		state.getProperties().forEach((property) -> {
			blockPropertyValues.put(property.getName(), state.getValue(property));
		});
		return shapeMap.getOrDefault(blockPropertyValues, shape);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		if (properties instanceof KubeJSBlockProperties kp) {
			for (var property : kp.blockBuilder.blockStateProperties) {
				builder.add(property);
			}
			kp.blockBuilder.blockStateProperties = Collections.unmodifiableSet(kp.blockBuilder.blockStateProperties);
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
		if (blockBuilder.placementStateModification != null) {
			var callbackJS = new BlockStateModifyPlacementCallbackJS(context, this);
			if (safeCallback(blockBuilder.placementStateModification, callbackJS, "Error while modifying BlockState placement of " + blockBuilder.id)) {
				return callbackJS.getState();
			}
		}

		if (!blockBuilder.canBeWaterlogged()) {
			return defaultBlockState();
		}

		return defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
	}

	@Override
	public boolean canBeReplaced(BlockState blockState, BlockPlaceContext context) {
		if (blockBuilder.canBeReplacedFunction != null) {
			var callbackJS = new CanBeReplacedCallbackJS(context, blockState);
			return blockBuilder.canBeReplacedFunction.test(callbackJS);
		}
		return super.canBeReplaced(blockState, context);
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
	public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (blockBuilder.randomTickCallback != null) {
			var callback = new RandomTickCallbackJS(new BlockContainerJS(level, pos), random);
			safeCallback(blockBuilder.randomTickCallback, callback, "Error while random ticking custom block ");
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
		} catch (Throwable e) {
			ScriptType.STARTUP.console.error(errorMessage, e);
			return false;
		}

		return true;
	}

	@Override
	public boolean canPlaceLiquid(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState, Fluid fluid) {
		if (blockBuilder.canBeWaterlogged()) {
			return SimpleWaterloggedBlock.super.canPlaceLiquid(blockGetter, blockPos, blockState, fluid);
		}

		return false;
	}

	@Override
	public boolean placeLiquid(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState, FluidState fluidState) {
		if (blockBuilder.canBeWaterlogged()) {
			return SimpleWaterloggedBlock.super.placeLiquid(levelAccessor, blockPos, blockState, fluidState);
		}

		return false;
	}

	@Override
	public ItemStack pickupBlock(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState) {
		if (blockBuilder.canBeWaterlogged()) {
			return SimpleWaterloggedBlock.super.pickupBlock(levelAccessor, blockPos, blockState);
		}

		return Items.STONE.getDefaultInstance();
	}

	@Override
	public Optional<SoundEvent> getPickupSound() {
		if (blockBuilder.canBeWaterlogged()) {
			return SimpleWaterloggedBlock.super.getPickupSound();
		}

		return Optional.empty();
	}

	@Override
	public void stepOn(Level level, BlockPos blockPos, BlockState blockState, Entity entity) {
		if (blockBuilder.stepOnCallback != null) {
			var callbackJS = new EntitySteppedOnBlockCallbackJS(level, entity, blockPos, blockState);
			safeCallback(blockBuilder.stepOnCallback, callbackJS, "Error while an entity stepped on custom block ");
		} else {
			super.stepOn(level, blockPos, blockState, entity);
		}
	}

	@Override
	public void fallOn(Level level, BlockState blockState, BlockPos blockPos, Entity entity, float f) {
		if (blockBuilder.fallOnCallback != null) {
			var callbackJS = new EntityFallenOnBlockCallbackJS(level, entity, blockPos, blockState, f);
			safeCallback(blockBuilder.fallOnCallback, callbackJS, "Error while an entity fell on custom block ");
		} else {
			super.fallOn(level, blockState, blockPos, entity, f);
		}
	}

	@Override
	public void updateEntityAfterFallOn(BlockGetter blockGetter, Entity entity) {
		if (blockBuilder.afterFallenOnCallback != null) {
			var callbackJS = new AfterEntityFallenOnBlockCallbackJS(blockGetter, entity);
			safeCallback(blockBuilder.afterFallenOnCallback, callbackJS, "Error while bouncing entity from custom block ");
			// if they did not change the entity's velocity, then use the default method to reset the velocity.
			if (!callbackJS.hasChangedVelocity()) {
				super.updateEntityAfterFallOn(blockGetter, entity);
			}
		} else {
			super.updateEntityAfterFallOn(blockGetter, entity);
		}
	}

	@Override
	public void wasExploded(Level level, BlockPos blockPos, Explosion explosion) {
		if (blockBuilder.explodedCallback != null) {
			var callbackJS = new BlockExplodedCallbackJS(level, blockPos, explosion);
			safeCallback(blockBuilder.explodedCallback, callbackJS, "Error while exploding custom block ");
		} else {
			super.wasExploded(level, blockPos, explosion);
		}
	}

	@Override
	public BlockState rotate(BlockState blockState, Rotation rotation) {
		if (blockBuilder.rotateStateModification != null) {
			var callbackJS = new BlockStateRotateCallbackJS(blockState, rotation);
			if (safeCallback(blockBuilder.rotateStateModification, callbackJS, "Error while rotating BlockState of ")) {
				return callbackJS.getState();
			}
		}

		return super.rotate(blockState, rotation);
	}

	@Override
	public BlockState mirror(BlockState blockState, Mirror mirror) {
		if (blockBuilder.mirrorStateModification != null) {
			var callbackJS = new BlockStateMirrorCallbackJS(blockState, mirror);
			if (safeCallback(blockBuilder.mirrorStateModification, callbackJS, "Error while mirroring BlockState of ")) {
				return callbackJS.getState();
			}
		}

		return super.mirror(blockState, mirror);
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (blockBuilder.rightClick != null) {
			if (!level.isClientSide()) {
				blockBuilder.rightClick.accept(new BlockRightClickedEventJS(player, hand, pos, hit.getDirection()));
			}

			return InteractionResult.SUCCESS;
		}

		return InteractionResult.PASS;
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean bl) {
		if (!state.is(newState.getBlock())) {
			if (level.getBlockEntity(pos) instanceof BlockEntityJS entity) {
				if (level instanceof ServerLevel) {
					for (var attachment : entity.attachments) {
						attachment.onRemove(newState);
					}
				}

				level.updateNeighbourForOutputSignal(pos, this);
			}

			super.onRemove(state, level, pos, newState, bl);
		}
	}

	@Override
	public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
		if (livingEntity != null && !level.isClientSide() && level.getBlockEntity(blockPos) instanceof BlockEntityJS e) {
			e.placerId = livingEntity.getUUID();
		}
	}

	@Override
	public ItemStack getCloneItemStack(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState) {
		if(blockBuilder.pickBlockCallback != null) {
			var callback = new PickBlockCallbackJS(blockGetter, blockPos, blockState);
			safeCallback(blockBuilder.pickBlockCallback, callback, "Error while getting pick block item ");
			if(callback.item != null) return new ItemStack(callback.item);
		}
		return super.getCloneItemStack(blockGetter, blockPos, blockState);
	}
}