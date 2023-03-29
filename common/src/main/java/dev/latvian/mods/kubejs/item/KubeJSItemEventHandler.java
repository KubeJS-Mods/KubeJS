package dev.latvian.mods.kubejs.item;

import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.InteractionEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.latvian.mods.kubejs.bindings.event.ItemEvents;
import dev.latvian.mods.kubejs.bindings.event.PlayerEvents;
import dev.latvian.mods.kubejs.player.InventoryChangedEventJS;
import dev.latvian.mods.kubejs.script.ScriptType;
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
		PlayerEvent.PICKUP_ITEM_PRE.register(KubeJSItemEventHandler::canPickUp);
		PlayerEvent.PICKUP_ITEM_POST.register(KubeJSItemEventHandler::pickup);
		PlayerEvent.DROP_ITEM.register(KubeJSItemEventHandler::drop);
		InteractionEvent.INTERACT_ENTITY.register(KubeJSItemEventHandler::entityInteract);
		PlayerEvent.CRAFT_ITEM.register(KubeJSItemEventHandler::crafted);
		PlayerEvent.SMELT_ITEM.register(KubeJSItemEventHandler::smelted);
	}

	private static CompoundEventResult<ItemStack> rightClick(Player player, InteractionHand hand) {
		var stack = player.getItemInHand(hand);

		if (!player.getCooldowns().isOnCooldown(stack.getItem())) {
			var result = ItemEvents.RIGHT_CLICKED.post(ScriptType.of(player), stack.getItem(), new ItemClickedEventJS(player, hand, stack));

			if (result.override()) {
				return result.archCompound();
			}
		}

		return CompoundEventResult.pass();
	}

	private static EventResult canPickUp(Player player, ItemEntity entity, ItemStack stack) {
		return ItemEvents.CAN_PICK_UP.post(ScriptType.of(player), stack.getItem(), new ItemPickedUpEventJS(player, entity, stack)).arch();
	}

	private static void pickup(Player player, ItemEntity entity, ItemStack stack) {
		ItemEvents.PICKED_UP.post(ScriptType.of(player), stack.getItem(), new ItemPickedUpEventJS(player, entity, stack));
	}

	private static EventResult drop(Player player, ItemEntity entity) {
		return ItemEvents.DROPPED.post(ScriptType.of(player), entity.getItem().getItem(), new ItemDroppedEventJS(player, entity)).arch();
	}

	private static EventResult entityInteract(Player player, Entity entity, InteractionHand hand) {
		return ItemEvents.ENTITY_INTERACTED.post(ScriptType.of(player), player.getItemInHand(hand).getItem(), new ItemEntityInteractedEventJS(player, entity, hand)).arch();
	}

	private static void crafted(Player player, ItemStack stack, Container grid) {
		if (!stack.isEmpty()) {
			ItemEvents.CRAFTED.post(ScriptType.of(player), stack.getItem(), new ItemCraftedEventJS(player, stack, grid));
			PlayerEvents.INVENTORY_CHANGED.post(ScriptType.of(player), stack.getItem(), new InventoryChangedEventJS(player, stack, -1));
		}
	}

	private static void smelted(Player player, ItemStack stack) {
		if (!stack.isEmpty()) {
			ItemEvents.SMELTED.post(ScriptType.of(player), stack.getItem(), new ItemSmeltedEventJS(player, stack));
			PlayerEvents.INVENTORY_CHANGED.post(ScriptType.of(player), stack.getItem(), new InventoryChangedEventJS(player, stack, -1));
		}
	}
}