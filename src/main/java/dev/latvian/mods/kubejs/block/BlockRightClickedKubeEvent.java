package dev.latvian.mods.kubejs.block;

import dev.latvian.mods.kubejs.level.BlockContainerJS;
import dev.latvian.mods.kubejs.player.KubePlayerEvent;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

@Info("""
	Invoked when a player right clicks on a block.
	""")
public class BlockRightClickedKubeEvent implements KubePlayerEvent {
	private ItemStack item;
	private final Player player;
	private final InteractionHand hand;
	private final BlockPos pos;
	private final Direction direction;
	private final BlockHitResult hitResult;
	private BlockContainerJS block;

	public BlockRightClickedKubeEvent(ItemStack item, Player player, InteractionHand hand, BlockPos pos, Direction direction, @Nullable BlockHitResult hitResult) {
		this.item = item;
		this.player = player;
		this.hand = hand;
		this.pos = pos;
		this.direction = direction;
		this.hitResult = hitResult;
	}

	@Override
	@Info("The player that right clicked the block.")
	public Player getEntity() {
		return player;
	}

	@Info("The block that was right clicked.")
	public BlockContainerJS getBlock() {
		if (block == null) {
			block = new BlockContainerJS(player.level(), pos);
		}

		return block;
	}

	@Info("The hand that was used to right click the block.")
	public InteractionHand getHand() {
		return hand;
	}

	@Info("The position of the block that was right clicked.")
	public ItemStack getItem() {
		if (item == null) {
			item = player.getItemInHand(hand);
		}

		return item;
	}

	@Info("The face of the block being right clicked.")
	public Direction getFacing() {
		return direction;
	}

	@Nullable
	public BlockHitResult getHitResult() {
		return hitResult;
	}
}