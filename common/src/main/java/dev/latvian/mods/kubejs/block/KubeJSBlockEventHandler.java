package dev.latvian.mods.kubejs.block;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.BlockEvent;
import dev.architectury.event.events.common.InteractionEvent;
import dev.architectury.utils.value.IntValue;
import dev.latvian.mods.kubejs.KubeJSEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * @author LatvianModder
 */
public class KubeJSBlockEventHandler {

	public static void init() {
		InteractionEvent.RIGHT_CLICK_BLOCK.register(KubeJSBlockEventHandler::rightClick);
		InteractionEvent.LEFT_CLICK_BLOCK.register(KubeJSBlockEventHandler::leftClick);
		BlockEvent.BREAK.register(KubeJSBlockEventHandler::blockBreak);
		BlockEvent.PLACE.register(KubeJSBlockEventHandler::blockPlace);
	}

	private static EventResult rightClick(Player player, InteractionHand hand, BlockPos pos, Direction direction) {
		if (player != null && player.level != null && !player.getCooldowns().isOnCooldown(player.getItemInHand(hand).getItem()) && new BlockRightClickEventJS(player, hand, pos, direction).post(KubeJSEvents.BLOCK_RIGHT_CLICK)) {
			return EventResult.interruptFalse();
		}

		return EventResult.pass();
	}

	private static EventResult leftClick(Player player, InteractionHand hand, BlockPos pos, Direction direction) {
		if (player != null && player.level != null && new BlockLeftClickEventJS(player, hand, pos, direction).post(KubeJSEvents.BLOCK_LEFT_CLICK)) {
			return EventResult.interruptFalse();
		}

		return EventResult.pass();
	}

	private static EventResult blockBreak(Level level, BlockPos pos, BlockState state, ServerPlayer player, @Nullable IntValue xp) {
		if (player != null && player.level != null && new BlockBreakEventJS(player, level, pos, state, xp).post(KubeJSEvents.BLOCK_BREAK)) {
			return EventResult.interruptFalse();
		}

		return EventResult.pass();
	}

	private static EventResult blockPlace(Level level, BlockPos pos, BlockState state, @Nullable Entity placer) {
		if (level != null && (placer == null || placer.level != null) && new BlockPlaceEventJS(placer, level, pos, state).post(KubeJSEvents.BLOCK_PLACE)) {
			return EventResult.interruptFalse();
		}

		return EventResult.pass();
	}

}