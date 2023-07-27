package dev.latvian.mods.kubejs.block.callbacks;

import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.level.BlockContainerJS;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class BlockStateModifyPlacementCallbackJS extends BlockStateModifyCallbackJS {
	public final BlockPlaceContext context;
	public final Block minecraftBlock;
	public BlockContainerJS block;

	public BlockStateModifyPlacementCallbackJS(BlockPlaceContext context, Block block) {
		super(getBlockStateToModify(context, block));
		this.context = context;
		this.minecraftBlock = block;
		this.block = new BlockContainerJS(context.getLevel(), context.getClickedPos());
	}

	private static BlockState getBlockStateToModify(BlockPlaceContext context, Block block) {
		BlockState previous = context.getLevel().getBlockState(context.getClickedPos());
		if (previous.getBlock() == block) {
			return previous;
		}
		return block.defaultBlockState();
	}

	public BlockPos getClickedPos() {
		return context.getClickedPos();
	}

	public BlockContainerJS getClickedBlock() {
		return new BlockContainerJS(getLevel(), getClickedPos());
	}

	public boolean canPlace() {
		return context.canPlace();
	}

	public boolean replacingClickedOnBlock() {
		return context.replacingClickedOnBlock();
	}

	public Direction getNearestLookingDirection() {
		return context.getNearestLookingDirection();
	}

	public Direction getNearestLookingVerticalDirection() {
		return context.getNearestLookingVerticalDirection();
	}

	public Direction[] getNearestLookingDirections() {
		return context.getNearestLookingDirections();
	}

	public Direction getClickedFace() {
		return context.getClickedFace();
	}

	public Vec3 getClickLocation() {
		return context.getClickLocation();
	}

	public boolean isInside() {
		return context.isInside();
	}

	public ItemStack getItem() {
		return ItemStackJS.of(context.getItemInHand());
	}

	@Nullable
	public Player getPlayer() {
		return context.getPlayer();
	}

	public InteractionHand getHand() {
		return context.getHand();
	}

	public Level getLevel() {
		return context.getLevel();
	}

	public Direction getHorizontalDirection() {
		return context.getHorizontalDirection();
	}

	public boolean isSecondaryUseActive() {
		return context.isSecondaryUseActive();
	}

	public float getRotation() {
		return context.getRotation();
	}

	public FluidState getFluidStateAtClickedPos() {
		return context.getLevel().getFluidState(context.getClickedPos());
	}

	public boolean isClickedPosIn(Fluid fluid) {
		return getFluidStateAtClickedPos().is(fluid);
	}

	public BlockStateModifyPlacementCallbackJS waterlogged(boolean waterlogged) {
		setValue(BlockStateProperties.WATERLOGGED, waterlogged);
		return this;
	}

	public BlockStateModifyPlacementCallbackJS waterlogged() {
		return waterlogged(isInWater());
	}

	public boolean isInWater() {
		return getFluidStateAtClickedPos().getType() == Fluids.WATER;
	}

	public boolean isReplacingSelf() {
		return getLevel().getBlockState(getClickedPos()).getBlock() == minecraftBlock;
	}
}
