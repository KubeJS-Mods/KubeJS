package dev.latvian.mods.kubejs.block;

import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.level.BlockContainerJS;
import dev.latvian.mods.kubejs.player.PlayerEventJS;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;

/**
 * @author LatvianModder
 */
public class BlockRightClickedEventJS extends PlayerEventJS {
	private final ServerPlayer player;
	private final InteractionHand hand;
	private final BlockPos pos;
	private final Direction direction;
	private BlockContainerJS block;
	private ItemStackJS item;

	public BlockRightClickedEventJS(ServerPlayer player, InteractionHand hand, BlockPos pos, Direction direction) {
		this.player = player;
		this.hand = hand;
		this.pos = pos;
		this.direction = direction;
	}

	@Override
	public ServerPlayer getEntity() {
		return player;
	}

	public BlockContainerJS getBlock() {
		if (block == null) {
			block = new BlockContainerJS(player.level, pos);
		}

		return block;
	}

	public InteractionHand getHand() {
		return hand;
	}

	public ItemStackJS getItem() {
		if (item == null) {
			item = ItemStackJS.of(player.getItemInHand(hand));
		}

		return item;
	}

	public Direction getFacing() {
		return direction;
	}
}