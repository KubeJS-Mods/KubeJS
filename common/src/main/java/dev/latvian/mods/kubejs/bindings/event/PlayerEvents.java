package dev.latvian.mods.kubejs.bindings.event;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.Extra;
import dev.latvian.mods.kubejs.player.ChestEventJS;
import dev.latvian.mods.kubejs.player.InventoryChangedEventJS;
import dev.latvian.mods.kubejs.player.InventoryEventJS;
import dev.latvian.mods.kubejs.player.PlayerAdvancementEventJS;
import dev.latvian.mods.kubejs.player.PlayerChatDecorateEventJS;
import dev.latvian.mods.kubejs.player.PlayerRespawnedEventJS;
import dev.latvian.mods.kubejs.player.SimplePlayerEventJS;
import dev.latvian.mods.kubejs.registry.KubeJSRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public interface PlayerEvents {
	Extra SUPPORTS_MENU_TYPE = new Extra().transformer(PlayerEvents::transformMenuType).identity();

	static MenuType<?> transformMenuType(Object o) {
		if (o == null) {
			return null;
		} else if (o instanceof MenuType<?> menuType) {
			return menuType;
		} else if (o instanceof AbstractContainerMenu menu) {
			try {
				return menu.getType();
			} catch (Exception ex) {
				return null;
			}
		}

		var id = ResourceLocation.tryParse(o.toString());
		return id == null ? null : KubeJSRegistries.menuTypes().get(id);
	}

	EventGroup GROUP = EventGroup.of("PlayerEvents");
	EventHandler LOGGED_IN = GROUP.server("loggedIn", () -> SimplePlayerEventJS.class);
	EventHandler LOGGED_OUT = GROUP.server("loggedOut", () -> SimplePlayerEventJS.class);
	EventHandler RESPAWNED = GROUP.server("respawned", () -> PlayerRespawnedEventJS.class);
	EventHandler TICK = GROUP.common("tick", () -> SimplePlayerEventJS.class);
	EventHandler CHAT = GROUP.server("chat", () -> PlayerChatDecorateEventJS.class).hasResult();
	EventHandler DECORATE_CHAT = GROUP.server("decorateChat", () -> PlayerChatDecorateEventJS.class);
	EventHandler ADVANCEMENT = GROUP.server("advancement", () -> PlayerAdvancementEventJS.class).extra(Extra.ID).hasResult();
	EventHandler INVENTORY_OPENED = GROUP.common("inventoryOpened", () -> InventoryEventJS.class).extra(SUPPORTS_MENU_TYPE);
	EventHandler INVENTORY_CLOSED = GROUP.common("inventoryClosed", () -> InventoryEventJS.class).extra(SUPPORTS_MENU_TYPE);
	EventHandler INVENTORY_CHANGED = GROUP.common("inventoryChanged", () -> InventoryChangedEventJS.class).extra(ItemEvents.SUPPORTS_ITEM);
	EventHandler CHEST_OPENED = GROUP.common("chestOpened", () -> ChestEventJS.class).extra(SUPPORTS_MENU_TYPE);
	EventHandler CHEST_CLOSED = GROUP.common("chestClosed", () -> ChestEventJS.class).extra(SUPPORTS_MENU_TYPE);
}
