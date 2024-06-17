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
		var key = state.getBlock().kjs$getRegistryKey();

		if (event.getLevel() instanceof Level level && BlockEvents.RIGHT_CLICKED.hasListeners(key) && !event.getEntity().getCooldowns().isOnCooldown(event.getEntity().getItemInHand(event.getHand()).getItem())) {
			BlockEvents.RIGHT_CLICKED.post(level, key, new BlockRightClickedKubeEvent(null, event.getEntity(), event.getHand(), event.getPos(), event.getFace(), event.getHitVec())).applyCancel(event);
		}
	}

	@SubscribeEvent
	public static void leftClick(PlayerInteractEvent.LeftClickBlock event) {
		var state = event.getLevel().getBlockState(event.getPos());
		var key = state.getBlock().kjs$getRegistryKey();

		if (event.getLevel() instanceof Level level && BlockEvents.LEFT_CLICKED.hasListeners(key)) {
			BlockEvents.LEFT_CLICKED.post(level, key, new BlockLeftClickedKubeEvent(event)).applyCancel(event);
		}
	}

	@SubscribeEvent
	public static void blockBreak(BlockEvent.BreakEvent event) {
		var key = event.getState().getBlock().kjs$getRegistryKey();

		if (event.getLevel() instanceof Level level && BlockEvents.BROKEN.hasListeners(key)) {
			BlockEvents.BROKEN.post(level, key, new BlockBrokenKubeEvent(event)).applyCancel(event);
		}
	}

	@SubscribeEvent
	public static void drops(BlockDropsEvent event) {
		var key = event.getState().getBlock().kjs$getRegistryKey();

		if (event.getLevel() instanceof ServerLevel level && BlockEvents.DROPS.hasListeners(key)) {
			BlockEvents.DROPS.post(level, key, new BlockDropsKubeEvent(event)).applyCancel(event);
		}
	}

	@SubscribeEvent
	public static void blockPlace(BlockEvent.EntityPlaceEvent event) {
		var key = event.getPlacedBlock().getBlock().kjs$getRegistryKey();

		if (event.getLevel() instanceof Level level && BlockEvents.PLACED.hasListeners(key)) {
			BlockEvents.PLACED.post(level, key, new BlockPlacedKubeEvent(event)).applyCancel(event);
		}
	}

	@SubscribeEvent
	public static void farmlandTrample(BlockEvent.FarmlandTrampleEvent event) {
		var key = event.getState().getBlock().kjs$getRegistryKey();

		if (event.getLevel() instanceof Level level && BlockEvents.FARMLAND_TRAMPLED.hasListeners(key)) {
			BlockEvents.FARMLAND_TRAMPLED.post(level, key, new FarmlandTrampledKubeEvent(event)).applyCancel(event);
		}
	}
}