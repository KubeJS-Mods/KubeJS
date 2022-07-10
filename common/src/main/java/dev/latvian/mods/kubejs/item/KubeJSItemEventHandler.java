package dev.latvian.mods.kubejs.item;

import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.InteractionEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.latvian.mods.kubejs.player.InventoryChangedEventJS;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * @author LatvianModder
 */
public class KubeJSItemEventHandler {
	public static void init() {
		InteractionEvent.RIGHT_CLICK_ITEM.register(KubeJSItemEventHandler::rightClick);
		InteractionEvent.CLIENT_RIGHT_CLICK_AIR.register(KubeJSItemEventHandler::rightClickEmpty);
		InteractionEvent.CLIENT_LEFT_CLICK_AIR.register(KubeJSItemEventHandler::leftClickEmpty);
		PlayerEvent.PICKUP_ITEM_PRE.register(KubeJSItemEventHandler::pickup);
		PlayerEvent.DROP_ITEM.register(KubeJSItemEventHandler::drop);
		InteractionEvent.INTERACT_ENTITY.register(KubeJSItemEventHandler::entityInteract);
		PlayerEvent.CRAFT_ITEM.register(KubeJSItemEventHandler::crafted);
		PlayerEvent.SMELT_ITEM.register(KubeJSItemEventHandler::smelted);
	}

	private static CompoundEventResult<ItemStack> rightClick(Player player, InteractionHand hand) {
		if (player != null && player.level instanceof ServerLevel && !player.getCooldowns().isOnCooldown(player.getItemInHand(hand).getItem()) && ItemRightClickEventJS.EVENT.post(new ItemRightClickEventJS(player, hand))) {
			return CompoundEventResult.interruptFalse(player.getItemInHand(hand));
		}

		return CompoundEventResult.pass();
	}

	private static void rightClickEmpty(Player player, InteractionHand hand) {
		if (player != null && player.level != null && player.level.isClientSide()) {
			ItemRightClickEmptyEventJS.EVENT.post(new ItemRightClickEmptyEventJS(player, hand));
		}
	}

	private static void leftClickEmpty(Player player, InteractionHand hand) {
		if (player != null && player.level != null && player.level.isClientSide()) {
			ItemLeftClickEventJS.EVENT.post(new ItemLeftClickEventJS(player, hand));
		}
	}

	private static EventResult pickup(Player player, ItemEntity entity, ItemStack stack) {
		if (player != null && entity != null && player.level instanceof ServerLevel && ItemPickupEventJS.EVENT.post(new ItemPickupEventJS(player, entity, stack))) {
			return EventResult.interruptFalse();
		}

		return EventResult.pass();
	}

	private static EventResult drop(Player player, ItemEntity entity) {
		if (player != null && entity != null && player.level instanceof ServerLevel && ItemTossEventJS.EVENT.post(new ItemTossEventJS(player, entity))) {
			return EventResult.interruptFalse();
		}

		return EventResult.pass();
	}

	private static EventResult entityInteract(Player player, Entity entity, InteractionHand hand) {
		if (player != null && entity != null && player.level instanceof ServerLevel && ItemEntityInteractEventJS.EVENT.post(new ItemEntityInteractEventJS(player, entity, hand))) {
			return EventResult.interruptFalse();
		}

		return EventResult.pass();
	}

	private static void crafted(Player player, ItemStack crafted, Container grid) {
		if (player instanceof ServerPlayer serverPlayer && !crafted.isEmpty()) {
			ItemCraftedEventJS.EVENT.post(new ItemCraftedEventJS(player, crafted, grid));
			InventoryChangedEventJS.EVENT.post(new InventoryChangedEventJS(serverPlayer, crafted, -1));
		}
	}

	private static void smelted(Player player, ItemStack smelted) {
		if (player instanceof ServerPlayer serverPlayer && !smelted.isEmpty()) {
			ItemSmeltedEventJS.EVENT.post(new ItemSmeltedEventJS(player, smelted));
			InventoryChangedEventJS.EVENT.post(new InventoryChangedEventJS(serverPlayer, smelted, -1));
		}
	}
}