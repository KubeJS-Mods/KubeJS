package dev.latvian.mods.kubejs.block;

import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.level.BlockContainerJS;
import dev.latvian.mods.kubejs.level.LevelJS;
import dev.latvian.mods.kubejs.player.PlayerJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
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
		super(block.defaultBlockState());
		this.context = context;
		this.minecraftBlock = block;
		this.block = new BlockContainerJS(context.getLevel(), context.getClickedPos());
	}

	public BlockPos getClickedPos() {
		return context.getClickedPos();
	}

	public BlockContainerJS getClickedBlock() {
		return getLevel().getBlock(context.getClickedPos());
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

	public ItemStackJS getItem() {
		return ItemStackJS.of(context.getItemInHand());
	}

	@Nullable
	public PlayerJS<?> getPlayer() {
		return getLevel().getPlayer(context.getPlayer());
	}

	public InteractionHand getHand() {
		return context.getHand();
	}

	public LevelJS getLevel() {
		return UtilsJS.getLevel(context.getLevel());
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
		waterlogged(isInWater());
		return this;
	}

	public boolean isInWater() {
		return getFluidStateAtClickedPos().getType() == Fluids.WATER;
	}
}
