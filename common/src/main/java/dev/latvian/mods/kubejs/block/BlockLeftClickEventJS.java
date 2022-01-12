package dev.latvian.mods.kubejs.block;

import dev.latvian.mods.kubejs.entity.EntityJS;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.level.BlockContainerJS;
import dev.latvian.mods.kubejs.player.PlayerEventJS;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

/**
 * @author LatvianModder
 */
public class BlockLeftClickEventJS extends PlayerEventJS {
	private final Player player;
	private final InteractionHand hand;
	private final BlockPos pos;
	private final Direction direction;

	public BlockLeftClickEventJS(Player player, InteractionHand hand, BlockPos pos, Direction direction) {
		this.player = player;
		this.hand = hand;
		this.pos = pos;
		this.direction = direction;
	}

	@Override
	public boolean canCancel() {
		return true;
	}

	@Override
	public EntityJS getEntity() {
		return entityOf(player);
	}

	public BlockContainerJS getBlock() {
		return new BlockContainerJS(player.level, pos);
	}

	public ItemStackJS getItem() {
		return ItemStackJS.of(player.getItemInHand(hand));
	}

	@Nullable
	public Direction getFacing() {
		return direction;
	}
}