package dev.latvian.mods.kubejs.block.callback;

import dev.latvian.mods.kubejs.level.LevelBlock;
import dev.latvian.mods.kubejs.typings.Info;
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

public class BlockStateModifyPlacementCallback extends BlockStateModifyCallback {
	public final BlockPlaceContext context;
	public final Block minecraftBlock;
	public LevelBlock block;

	public BlockStateModifyPlacementCallback(BlockPlaceContext context, Block block) {
		super(getBlockStateToModify(context, block));
		this.context = context;
		this.minecraftBlock = block;
		this.block = context.getLevel().kjs$getBlock(context.getClickedPos());
	}

	private static BlockState getBlockStateToModify(BlockPlaceContext context, Block block) {
		BlockState previous = context.getLevel().getBlockState(context.getClickedPos());
		if (previous.getBlock() == block) {
			return previous;
		}
		return block.defaultBlockState();
	}

	@Info("Gets the clicked position in world")
	public BlockPos getClickedPos() {
		return context.getClickedPos();
	}

	@Info("Gets the clicked block")
	public LevelBlock getClickedBlock() {
		return getLevel().kjs$getBlock(getClickedPos());
	}

	@Info("Returns if the block being placed thinks it can be placed here. This is used for replacement checks, like placing blocks in water or tall grass")
	public boolean canPlace() {
		return context.canPlace();
	}

	@Info("Returns if the block being placed is replacing the block clicked")
	public boolean replacingClickedOnBlock() {
		return context.replacingClickedOnBlock();
	}

	@Info("Gets the direction closes to where the player is currently looking")
	public Direction getNearestLookingDirection() {
		return context.getNearestLookingDirection();
	}

	@Info("Gets the vertical direction (UP/DOWN) closest to where the player is currently looking")
	public Direction getNearestLookingVerticalDirection() {
		return context.getNearestLookingVerticalDirection();
	}

	@Info("Gets an array of all directions, ordered by which the player is looking closest to")
	public Direction[] getNearestLookingDirections() {
		return context.getNearestLookingDirections();
	}

	@Info("Gets the facing direction of the clicked block face")
	public Direction getClickedFace() {
		return context.getClickedFace();
	}

	@Info("Gets the position in the block-space of where it was clicked")
	public Vec3 getClickLocation() {
		return context.getClickLocation();
	}

	@Info("Returns if the hit posiiton in the block-space is inside the 1x1x1 cube of the block")
	public boolean isInside() {
		return context.isInside();
	}

	@Info("Gets the item being placed")
	public ItemStack getItem() {
		return context.getItemInHand();
	}

	@Nullable
	@Info("Gets the player placing the block, if available")
	public Player getPlayer() {
		return context.getPlayer();
	}

	@Info("Gets the hand that is placing the block")
	public InteractionHand getHand() {
		return context.getHand();
	}

	@Info("Gets the level")
	public Level getLevel() {
		return context.getLevel();
	}

	@Info("Gets the nearest horizontal direction to where the player is looking. NORTH if there is no player")
	public Direction getHorizontalDirection() {
		return context.getHorizontalDirection();
	}

	@Info("Returns if the player is using the 'secondary' function of this item. Basically checks if they are holding shift")
	public boolean isSecondaryUseActive() {
		return context.isSecondaryUseActive();
	}

	@Info("Get the horizontal rotation of the player")
	public float getRotation() {
		return context.getRotation();
	}

	@Info("Gets the FluidSate at the clicked position")
	public FluidState getFluidStateAtClickedPos() {
		return context.getLevel().getFluidState(context.getClickedPos());
	}

	@Info("Checks if the position clicked has a specified fluid there")
	public boolean isClickedPosIn(Fluid fluid) {
		return getFluidStateAtClickedPos().is(fluid);
	}

	@Info("Set if this block is waterlogged or not")
	public BlockStateModifyPlacementCallback waterlogged(boolean waterlogged) {
		setValue(BlockStateProperties.WATERLOGGED, waterlogged);
		return this;
	}

	@Info("Set this block as waterlogged if it is in water")
	public BlockStateModifyPlacementCallback waterlogged() {
		return waterlogged(isInWater());
	}

	@Info("Checks if this block is in water")
	public boolean isInWater() {
		return getFluidStateAtClickedPos().getType() == Fluids.WATER;
	}

	@Info("""
		Checks if the block currently occupying the position this is being placed in is the same block type. 
		Used for things like candles, where multiple can be in the same block-space.
		""")
	public boolean isReplacingSelf() {
		return getLevel().getBlockState(getClickedPos()).getBlock() == minecraftBlock;
	}
}
