package dev.latvian.mods.kubejs.block.custom;

import dev.latvian.mods.kubejs.block.BlockBuilder;
import dev.latvian.mods.kubejs.client.VariantBlockStateGenerator;
import dev.latvian.mods.kubejs.generator.KubeAssetGenerator;
import dev.latvian.mods.kubejs.util.ID;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@ReturnsSelf
// Pillar blocks that can face any axis (XYZ).
public class PillarBlockBuilder extends BlockBuilder {
	private static final ResourceLocation MODEL = ResourceLocation.withDefaultNamespace("block/cube_column");

	public PillarBlockBuilder(ResourceLocation i) {
		super(i);
	}

	@Override
	protected void generateBlockState(VariantBlockStateGenerator bs) {
		var modelLocation = parentModel == null ? id.withPath(ID.BLOCK) : parentModel;
		bs.variant("axis=x", v -> v.model(modelLocation).x(90).y(90));
		bs.variant("axis=y", v -> v.model(modelLocation));
		bs.variant("axis=z", v -> v.model(modelLocation).x(90));
	}

	@Override
	protected void generateBlockModels(KubeAssetGenerator gen) {
		gen.blockModel(id, mg -> {
			var side = textures.getOrDefault("side", baseTexture);

			mg.texture("side", side);
			mg.texture("end", textures.getOrDefault("end", newID("block/", "_top").toString()));

			mg.parent(parentModel == null ? MODEL : parentModel);
		});
	}

	@Override
	public Block createObject() {
		return blockEntityInfo != null ? new WithEntity(this) : new PillarKubeBlock(this);
	}

	public static class PillarKubeBlock extends BasicKubeBlock {
		public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;
		public final @Nullable VoxelShape shapeX;
		public final @Nullable VoxelShape shapeY;
		public final @Nullable VoxelShape shapeZ;

		public PillarKubeBlock(BlockBuilder p) {
			super(p);
			shapeX = hasCustomShape() ? rotateShape(shape, Direction.Axis.X) : null;
			shapeY = hasCustomShape() ? rotateShape(shape, Direction.Axis.Y) : null;
			shapeZ = hasCustomShape() ? rotateShape(shape, Direction.Axis.Z) : null;
		}

		private static VoxelShape rotateShape(VoxelShape shape, Direction.Axis axis) {
			List<AABB> newShapes = new ArrayList<>();

			switch (axis) {
				case Y -> {
					return shape;
				}
				case X -> shape.forAllBoxes((x1, y1, z1, x2, y2, z2) -> newShapes.add(new AABB(y1, z1, x1, y2, z2, x2)));
				case Z -> shape.forAllBoxes((x1, y1, z1, x2, y2, z2) -> newShapes.add(new AABB(x1, z1, 1D - y2, x2, z2, 1D - y1)));
				default -> throw new IllegalArgumentException("Cannot rotate around axis " + axis.getName());
			}
			return createShape(newShapes);
		}

		@Override
		protected void createBlockStateDefinition(@NotNull StateDefinition.Builder<Block, BlockState> builder) {
			builder.add(AXIS);
			super.createBlockStateDefinition(builder);
		}

		@Override
		public BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
			var state = defaultBlockState().setValue(AXIS, context.getClickedFace().getAxis());

			if (blockBuilder.canBeWaterlogged()) {
				state = state.setValue(BlockStateProperties.WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
			}

			return state;
		}

		private boolean hasCustomShape() {
			return shape != Shapes.block();
		}

		@Override
		@Deprecated
		public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
			return hasCustomShape() ? switch (state.getValue(AXIS)) {
				case X -> {
					assert shapeX != null;
					yield shapeX;
				}
				case Y -> {
					assert shapeY != null;
					yield shapeY;
				}
				case Z -> {
					assert shapeZ != null;
					yield shapeZ;
				}
			} : shape;
		}
	}

	public static class WithEntity extends PillarKubeBlock implements EntityBlock {
		public WithEntity(BlockBuilder p) {
			super(p);
		}

		@Nullable
		@Override
		public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
			return blockBuilder.blockEntityInfo.createBlockEntity(pos, state);
		}

		@Nullable
		@Override
		public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
			return blockBuilder.blockEntityInfo.getTicker(level);
		}
	}
}
