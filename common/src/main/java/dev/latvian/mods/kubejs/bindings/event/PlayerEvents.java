package dev.latvian.mods.kubejs.bindings.event;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.Extra;
import dev.latvian.mods.kubejs.player.ChestEventJS;
import dev.latvian.mods.kubejs.player.InventoryChangedEventJS;
import dev.latvian.mods.kubejs.player.InventoryEventJS;
import dev.latvian.mods.kubejs.player.LeftClickedHandEventJS;
import dev.latvian.mods.kubejs.player.PlayerAdvancementEventJS;
import dev.latvian.mods.kubejs.player.PlayerChatDecorateEventJS;
import dev.latvian.mods.kubejs.player.PlayerClonedEventJS;
import dev.latvian.mods.kubejs.player.RightClickeHandEventJS;
import dev.latvian.mods.kubejs.player.SimplePlayerEventJS;

public interface PlayerEvents {
	EventGroup GROUP = EventGroup.of("PlayerEvents");
	EventHandler LOGGED_IN = GROUP.server("loggedIn", () -> SimplePlayerEventJS.class);
	EventHandler LOGGED_OUT = GROUP.server("loggedOut", () -> SimplePlayerEventJS.class);
	EventHandler CLONED = GROUP.server("cloned", () -> PlayerClonedEventJS.class);
	EventHandler TICK = GROUP.server("tick", () -> SimplePlayerEventJS.class);
	EventHandler CHAT = GROUP.server("chat", () -> PlayerChatDecorateEventJS.class).cancelable();
	EventHandler DECORATE_CHAT = GROUP.server("decorateChat", () -> PlayerChatDecorateEventJS.class);
	EventHandler ADVANCEMENT = GROUP.server("advancement", () -> PlayerAdvancementEventJS.class).extra(Extra.ID).cancelable();
	EventHandler INVENTORY_OPENED = GROUP.server("inventoryOpened", () -> InventoryEventJS.class);
	EventHandler INVENTORY_CLOSED = GROUP.server("inventoryClosed", () -> InventoryEventJS.class);
	EventHandler INVENTORY_CHANGED = GROUP.server("inventoryChanged", () -> InventoryChangedEventJS.class).extra(ItemEvents.SUPPORTS_ITEM);
	EventHandler CHEST_OPENED = GROUP.server("chestOpened", () -> ChestEventJS.class);
	EventHandler CHEST_CLOSED = GROUP.server("chestClosed", () -> ChestEventJS.class);
	EventHandler RIGHT_CLICKED_HAND = GROUP.client("rightClickedHand", () -> RightClickeHandEventJS.class);
	EventHandler LEFT_CLICKED_HAND = GROUP.client("leftClickedHand", () -> LeftClickedHandEventJS.class);
}
