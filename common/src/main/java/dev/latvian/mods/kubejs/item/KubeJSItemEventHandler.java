package dev.latvian.mods.kubejs.item;

import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.InteractionEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.latvian.mods.kubejs.bindings.ItemWrapper;
import dev.latvian.mods.kubejs.bindings.event.ItemEvents;
import dev.latvian.mods.kubejs.bindings.event.PlayerEvents;
import dev.latvian.mods.kubejs.player.InventoryChangedEventJS;
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

	private static String getItemId(ItemStack stack) {
		return String.valueOf(ItemWrapper.getId(stack.getItem()));
	}

	private static CompoundEventResult<ItemStack> rightClick(Player player, InteractionHand hand) {
		if (player instanceof ServerPlayer p && !player.getCooldowns().isOnCooldown(player.getItemInHand(hand).getItem()) && ItemEvents.RIGHT_CLICKED.post(getItemId(p.getItemInHand(hand)), new ItemRightClickedEventJS(p, hand))) {
			return CompoundEventResult.interruptFalse(player.getItemInHand(hand));
		}

		return CompoundEventResult.pass();
	}

	private static void rightClickEmpty(Player player, InteractionHand hand) {
		if (player != null && player.level != null && player.level.isClientSide()) {
			ItemEvents.RIGHT_CLICKED_EMPTY.post(getItemId(player.getItemInHand(hand)), new ItemRightClickedEmptyEventJS(player, hand));
		}
	}

	private static void leftClickEmpty(Player player, InteractionHand hand) {
		if (player != null && player.level != null && player.level.isClientSide()) {
			ItemEvents.LEFT_CLICKED.post(getItemId(player.getItemInHand(hand)), new ItemLeftClickedEventJS(player, hand));
		}
	}

	private static EventResult pickup(Player player, ItemEntity entity, ItemStack stack) {
		if (player instanceof ServerPlayer p && entity != null && ItemEvents.PICKED_UP.post(getItemId(stack), new ItemPickedUpEventJS(p, entity, stack))) {
			return EventResult.interruptFalse();
		}

		return EventResult.pass();
	}

	private static EventResult drop(Player player, ItemEntity entity) {
		if (player instanceof ServerPlayer p && entity != null && ItemEvents.DROPPED.post(getItemId(entity.getItem()), new ItemDroppedEventJS(p, entity))) {
			return EventResult.interruptFalse();
		}

		return EventResult.pass();
	}

	private static EventResult entityInteract(Player player, Entity entity, InteractionHand hand) {
		if (player instanceof ServerPlayer p && entity != null && ItemEvents.ENTITY_INTERACTED.post(getItemId(p.getItemInHand(hand)), new ItemEntityInteractedEventJS(p, entity, hand))) {
			return EventResult.interruptFalse();
		}

		return EventResult.pass();
	}

	private static void crafted(Player player, ItemStack stack, Container grid) {
		if (player instanceof ServerPlayer serverPlayer && !stack.isEmpty()) {
			String id = getItemId(stack);
			ItemEvents.CRAFTED.post(id, new ItemCraftedEventJS(serverPlayer, stack, grid));
			PlayerEvents.INVENTORY_CHANGED.post(id, new InventoryChangedEventJS(serverPlayer, stack, -1));
		}
	}

	private static void smelted(Player player, ItemStack stack) {
		if (player instanceof ServerPlayer serverPlayer && !stack.isEmpty()) {
			String id = getItemId(stack);
			ItemEvents.SMELTED.post(id, new ItemSmeltedEventJS(serverPlayer, stack));
			PlayerEvents.INVENTORY_CHANGED.post(id, new InventoryChangedEventJS(serverPlayer, stack, -1));
		}
	}
}