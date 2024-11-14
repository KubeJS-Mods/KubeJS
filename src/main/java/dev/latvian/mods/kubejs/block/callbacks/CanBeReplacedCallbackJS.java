package dev.latvian.mods.kubejs.block.callbacks;

import dev.latvian.mods.kubejs.level.LevelBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class CanBeReplacedCallbackJS {

	private final BlockPlaceContext context;

	public CanBeReplacedCallbackJS(BlockPlaceContext blockPlaceContext, BlockState state) {
		context = blockPlaceContext;
	}

	public BlockPos getClickedPos() {
		return context.getClickedPos();
	}

	public LevelBlock getClickedBlock() {
		return getLevel().kjs$getBlock(getClickedPos());
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
		return context.getItemInHand();
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

	public boolean canBeReplaced() {
		return getLevel().getBlockState(getClickedPos()).canBeReplaced();
	}
}
