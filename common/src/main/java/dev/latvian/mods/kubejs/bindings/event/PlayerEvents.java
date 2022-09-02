package dev.latvian.mods.kubejs.bindings.event;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.player.ChestEventJS;
import dev.latvian.mods.kubejs.player.InventoryChangedEventJS;
import dev.latvian.mods.kubejs.player.InventoryEventJS;
import dev.latvian.mods.kubejs.player.PlayerAdvancementEventJS;
import dev.latvian.mods.kubejs.player.PlayerChatDecorateEventJS;
import dev.latvian.mods.kubejs.player.PlayerClonedEventJS;
import dev.latvian.mods.kubejs.player.SimplePlayerEventJS;

public interface PlayerEvents {
	EventGroup GROUP = EventGroup.of("PlayerEvents");
	EventHandler LOGGED_IN = GROUP.server("loggedIn", () -> SimplePlayerEventJS.class).legacy("player.logged_in");
	EventHandler LOGGED_OUT = GROUP.server("loggedOut", () -> SimplePlayerEventJS.class).legacy("player.logged_out");
	EventHandler CLONED = GROUP.server("cloned", () -> PlayerClonedEventJS.class);
	EventHandler TICK = GROUP.server("tick", () -> SimplePlayerEventJS.class).legacy("player.tick");
	EventHandler CHAT = GROUP.server("chat", () -> PlayerChatDecorateEventJS.class).cancelable().legacy("player.chat");
	EventHandler DECORATE_CHAT = GROUP.server("decorateChat", () -> PlayerChatDecorateEventJS.class);
	EventHandler ADVANCEMENT = GROUP.server("advancement", () -> PlayerAdvancementEventJS.class).supportsNamespacedExtraId().cancelable().legacy("player.advancement");
	EventHandler INVENTORY_OPENED = GROUP.server("inventoryOpened", () -> InventoryEventJS.class).legacy("player.inventory.opened");
	EventHandler INVENTORY_CLOSED = GROUP.server("inventoryClosed", () -> InventoryEventJS.class).legacy("player.inventory.closed");
	EventHandler INVENTORY_CHANGED = GROUP.server("inventoryChanged", () -> InventoryChangedEventJS.class).supportsNamespacedExtraId().legacy("player.inventory.changed");
	EventHandler CHEST_OPENED = GROUP.server("chestOpened", () -> ChestEventJS.class).legacy("player.chest.opened");
	EventHandler CHEST_CLOSED = GROUP.server("chestClosed", () -> ChestEventJS.class).legacy("player.chest.closed");
}
