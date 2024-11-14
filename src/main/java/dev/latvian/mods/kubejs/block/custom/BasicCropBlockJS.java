package dev.latvian.mods.kubejs.block.custom;

import dev.latvian.mods.kubejs.block.KubeJSBlockProperties;
import dev.latvian.mods.kubejs.block.RandomTickCallbackJS;
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
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BasicCropBlockJS extends CropBlock {
	private final CropBlockBuilder builder;
	private IntegerProperty ageProperty;

	public BasicCropBlockJS(CropBlockBuilder builder) {
		super(builder.createProperties().sound(SoundType.CROP).randomTicks());
		this.builder = builder;
	}

	@Override
	public IntegerProperty getAgeProperty() {
		if (ageProperty == null) {
			ageProperty = IntegerProperty.create("age", 0, ((CropBlockBuilder) ((KubeJSBlockProperties) properties).blockBuilder).age);
		}

		return ageProperty;
	}

	@Override
	public int getMaxAge() {
		return builder.age;
	}

	@Override
	protected ItemLike getBaseSeedId() {
		return builder.itemBuilder != null ? builder.itemBuilder.get() : Items.AIR;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(getAgeProperty());
	}

	@Override
	public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
		return builder.shapeByAge.get(blockState.getValue(this.getAgeProperty()));
	}

	@Override
	public void randomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource random) {
		double f = builder.growSpeedCallback == null ? -1 : builder.growSpeedCallback.applyAsDouble(new RandomTickCallbackJS(serverLevel.kjs$getBlock(blockPos).cache(blockState), random));
		int age = this.getAge(blockState);
		if (age < this.getMaxAge()) {
			if (f < 0) {
				f = getGrowthSpeed(blockState, serverLevel, blockPos);
			}
			if (f > 0 && random.nextInt((int) (25.0F / f) + 1) == 0) {
				serverLevel.setBlock(blockPos, this.getStateForAge(age + 1), 2);
			}
		}
	}

	@Override
	public void growCrops(Level level, BlockPos blockPos, BlockState blockState) {
		if (builder.fertilizerCallback == null) {
			super.growCrops(level, blockPos, blockState);
		} else {
			int effect = builder.fertilizerCallback.applyAsInt(new RandomTickCallbackJS(level.kjs$getBlock(blockPos).cache(blockState), level.random));
			if (effect > 0) {
				level.setBlock(blockPos, this.getStateForAge(Integer.min(getAge(blockState) + effect, getMaxAge())), 2);
			}
		}
	}

	@Override
	public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
		return builder.surviveCallback != null ? builder.surviveCallback.survive(blockState, levelReader, blockPos) : super.canSurvive(blockState, levelReader, blockPos);
	}
}
