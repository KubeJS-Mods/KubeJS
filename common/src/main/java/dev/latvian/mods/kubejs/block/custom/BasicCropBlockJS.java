package dev.latvian.mods.kubejs.block.custom;

import dev.latvian.mods.kubejs.block.RandomTickCallbackJS;
import dev.latvian.mods.kubejs.item.ItemBuilder;
import dev.latvian.mods.kubejs.level.BlockContainerJS;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;

public class BasicCropBlockJS extends CropBlock {
	private final int age;
	private final ItemBuilder seedItem;
	private final List<VoxelShape> shapeByAge;
	private final boolean dropSeed;
	private final ToDoubleFunction<RandomTickCallbackJS> growSpeedCallback;
	private final ToIntFunction<RandomTickCallbackJS> fertilizerCallback;
	private final CropBlockBuilder.SurviveCallback surviveCallback;

	public BasicCropBlockJS(CropBlockBuilder builder) {
		super(builder.createProperties().sound(SoundType.CROP).randomTicks());
		age = builder.age;
		seedItem = builder.itemBuilder;
		shapeByAge = builder.shapeByAge;
		dropSeed = builder.dropSeed;
		growSpeedCallback = builder.growSpeedCallback;
		fertilizerCallback = builder.fertilizerCallback;
		surviveCallback = builder.surviveCallback;
	}

	@Override
	public int getMaxAge() {
		return age;
	}

	@Override
	protected ItemLike getBaseSeedId() {
		return dropSeed ? seedItem.get() : Items.AIR;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(getAgeProperty());
	}

	@Override
	public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
		return shapeByAge.get(blockState.getValue(this.getAgeProperty()));
	}

	@Override
	public void randomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource random) {
		double f = growSpeedCallback == null ? -1 : growSpeedCallback.applyAsDouble(new RandomTickCallbackJS(new BlockContainerJS(serverLevel, blockPos), random));
		int age = this.getAge(blockState);
		if (age < this.getMaxAge()) {
			if (f < 0) {
				f = getGrowthSpeed(this, serverLevel, blockPos);
			}
			if (f > 0 && random.nextInt((int) (25.0F / f) + 1) == 0) {
				serverLevel.setBlock(blockPos, this.getStateForAge(age + 1), 2);
			}
		}
	}

	@Override
	public void growCrops(Level level, BlockPos blockPos, BlockState blockState) {
		if (fertilizerCallback == null) {
			super.growCrops(level, blockPos, blockState);
		} else {
			int effect = fertilizerCallback.applyAsInt(new RandomTickCallbackJS(new BlockContainerJS(level, blockPos), level.random));
			if (effect > 0) {
				level.setBlock(blockPos, this.getStateForAge(Integer.min(getAge(blockState) + effect, getMaxAge())), 2);
			}
		}
	}

	@Override
	public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
		return surviveCallback != null ?
			surviveCallback.survive(blockState, levelReader, blockPos) :
			super.canSurvive(blockState, levelReader, blockPos);
	}
}
