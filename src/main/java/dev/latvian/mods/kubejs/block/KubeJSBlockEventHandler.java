package dev.latvian.mods.kubejs.block;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.bindings.event.BlockEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

@EventBusSubscriber(modid = KubeJS.MOD_ID)
public class KubeJSBlockEventHandler {
	@SubscribeEvent
	public static void rightClick(PlayerInteractEvent.RightClickBlock event) {
		var state = event.getLevel().getBlockState(event.getPos());

		if (event.getLevel() instanceof Level level && BlockEvents.RIGHT_CLICKED.hasListeners(state.kjs$getRegistryKey()) && !event.getEntity().getCooldowns().isOnCooldown(event.getEntity().getItemInHand(event.getHand()).getItem())) {
			BlockEvents.RIGHT_CLICKED.post(level, state.kjs$getRegistryKey(), new BlockRightClickedKubeEvent(null, event.getEntity(), event.getHand(), event.getPos(), event.getFace(), event.getHitVec())).applyCancel(event);
		}
	}

	@SubscribeEvent
	public static void leftClick(PlayerInteractEvent.LeftClickBlock event) {
		var state = event.getLevel().getBlockState(event.getPos());

		if (event.getLevel() instanceof Level level && BlockEvents.LEFT_CLICKED.hasListeners(state.kjs$getRegistryKey())) {
			BlockEvents.LEFT_CLICKED.post(level, state.kjs$getRegistryKey(), new BlockLeftClickedKubeEvent(event)).applyCancel(event);
		}
	}

	@SubscribeEvent
	public static void blockBreak(BlockEvent.BreakEvent event) {
		if (event.getLevel() instanceof Level level && BlockEvents.BROKEN.hasListeners(event.getState().kjs$getRegistryKey())) {
			BlockEvents.BROKEN.post(level, event.getState().kjs$getRegistryKey(), new BlockBrokenKubeEvent(event)).applyCancel(event);
		}
	}

	@SubscribeEvent
	public static void drops(BlockDropsEvent event) {
		if (event.getLevel() instanceof ServerLevel level && BlockEvents.DROPS.hasListeners(event.getState().kjs$getRegistryKey())) {
			BlockEvents.DROPS.post(level, event.getState().kjs$getRegistryKey(), new BlockDropsKubeEvent(event)).applyCancel(event);
		}
	}

	@SubscribeEvent
	public static void blockPlace(BlockEvent.EntityPlaceEvent event) {
		if (event.getLevel() instanceof Level level && BlockEvents.PLACED.hasListeners(event.getPlacedBlock().kjs$getRegistryKey())) {
			BlockEvents.PLACED.post(level, event.getPlacedBlock().kjs$getRegistryKey(), new BlockPlacedKubeEvent(event)).applyCancel(event);
		}
	}

	@SubscribeEvent
	public static void farmlandTrample(BlockEvent.FarmlandTrampleEvent event) {
		if (event.getLevel() instanceof Level level && BlockEvents.FARMLAND_TRAMPLED.hasListeners(event.getState().kjs$getRegistryKey())) {
			BlockEvents.FARMLAND_TRAMPLED.post(level, event.getState().kjs$getRegistryKey(), new FarmlandTrampledKubeEvent(event)).applyCancel(event);
		}
	}
}