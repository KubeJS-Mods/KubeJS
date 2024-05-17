package dev.latvian.mods.kubejs.bindings.event;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.Extra;
import dev.latvian.mods.kubejs.player.ChestKubeEvent;
import dev.latvian.mods.kubejs.player.InventoryChangedKubeEvent;
import dev.latvian.mods.kubejs.player.InventoryKubeEvent;
import dev.latvian.mods.kubejs.player.PlayerAdvancementKubeEvent;
import dev.latvian.mods.kubejs.player.PlayerChatDecorateKubeEvent;
import dev.latvian.mods.kubejs.player.PlayerRespawnedKubeEvent;
import dev.latvian.mods.kubejs.player.SimplePlayerKubeEvent;
import dev.latvian.mods.kubejs.player.StageChangedEvent;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public interface PlayerEvents {
	Extra SUPPORTS_MENU_TYPE = new Extra().transformer(PlayerEvents::transformMenuType).identity().describeType(context -> context.javaType(MenuType.class));

	static Object transformMenuType(Object o) {
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
		return id == null ? null : RegistryInfo.MENU.getValue(id);
	}

	EventGroup GROUP = EventGroup.of("PlayerEvents");
	EventHandler LOGGED_IN = GROUP.server("loggedIn", () -> SimplePlayerKubeEvent.class);
	EventHandler LOGGED_OUT = GROUP.server("loggedOut", () -> SimplePlayerKubeEvent.class);
	EventHandler RESPAWNED = GROUP.server("respawned", () -> PlayerRespawnedKubeEvent.class);
	EventHandler TICK = GROUP.common("tick", () -> SimplePlayerKubeEvent.class);
	EventHandler CHAT = GROUP.server("chat", () -> PlayerChatDecorateKubeEvent.class).hasResult();
	EventHandler DECORATE_CHAT = GROUP.server("decorateChat", () -> PlayerChatDecorateKubeEvent.class);
	EventHandler ADVANCEMENT = GROUP.server("advancement", () -> PlayerAdvancementKubeEvent.class).extra(Extra.ID).hasResult();
	EventHandler INVENTORY_OPENED = GROUP.common("inventoryOpened", () -> InventoryKubeEvent.class).extra(SUPPORTS_MENU_TYPE);
	EventHandler INVENTORY_CLOSED = GROUP.common("inventoryClosed", () -> InventoryKubeEvent.class).extra(SUPPORTS_MENU_TYPE);
	EventHandler INVENTORY_CHANGED = GROUP.common("inventoryChanged", () -> InventoryChangedKubeEvent.class).extra(ItemEvents.SUPPORTS_ITEM);
	EventHandler CHEST_OPENED = GROUP.common("chestOpened", () -> ChestKubeEvent.class).extra(SUPPORTS_MENU_TYPE);
	EventHandler CHEST_CLOSED = GROUP.common("chestClosed", () -> ChestKubeEvent.class).extra(SUPPORTS_MENU_TYPE);
	EventHandler STAGE_ADDED = GROUP.common("stageAdded", () -> StageChangedEvent.class).extra(Extra.STRING);
	EventHandler STAGE_REMOVED = GROUP.common("stageRemoved", () -> StageChangedEvent.class).extra(Extra.STRING);
}
