package dev.latvian.mods.kubejs.bindings.event;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.Extra;
import dev.latvian.mods.kubejs.event.SpecializedEventHandler;
import dev.latvian.mods.kubejs.player.ChestKubeEvent;
import dev.latvian.mods.kubejs.player.InventoryChangedKubeEvent;
import dev.latvian.mods.kubejs.player.InventoryKubeEvent;
import dev.latvian.mods.kubejs.player.PlayerAdvancementKubeEvent;
import dev.latvian.mods.kubejs.player.PlayerChatReceivedKubeEvent;
import dev.latvian.mods.kubejs.player.PlayerClonedKubeEvent;
import dev.latvian.mods.kubejs.player.PlayerRespawnedKubeEvent;
import dev.latvian.mods.kubejs.player.SimplePlayerKubeEvent;
import dev.latvian.mods.kubejs.player.StageChangedEvent;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;

public interface PlayerEvents {
	Extra<ResourceKey<MenuType<?>>> SUPPORTS_MENU_TYPE = Extra.registryKey(Registries.MENU, MenuType.class);

	EventGroup GROUP = EventGroup.of("PlayerEvents");
	EventHandler LOGGED_IN = GROUP.server("loggedIn", () -> SimplePlayerKubeEvent.class);
	EventHandler LOGGED_OUT = GROUP.server("loggedOut", () -> SimplePlayerKubeEvent.class);
	EventHandler CLONED = GROUP.server("cloned", () -> PlayerClonedKubeEvent.class);
	EventHandler RESPAWNED = GROUP.server("respawned", () -> PlayerRespawnedKubeEvent.class);
	EventHandler TICK = GROUP.common("tick", () -> SimplePlayerKubeEvent.class);
	EventHandler DECORATE_CHAT = GROUP.server("decorateChat", () -> PlayerChatReceivedKubeEvent.class).hasResult();
	EventHandler CHAT = GROUP.server("chat", () -> PlayerChatReceivedKubeEvent.class).hasResult();
	SpecializedEventHandler<ResourceLocation> ADVANCEMENT = GROUP.server("advancement", Extra.ID, () -> PlayerAdvancementKubeEvent.class).hasResult();
	SpecializedEventHandler<ResourceKey<MenuType<?>>> INVENTORY_OPENED = GROUP.common("inventoryOpened", SUPPORTS_MENU_TYPE, () -> InventoryKubeEvent.class);
	SpecializedEventHandler<ResourceKey<MenuType<?>>> INVENTORY_CLOSED = GROUP.common("inventoryClosed", SUPPORTS_MENU_TYPE, () -> InventoryKubeEvent.class);
	SpecializedEventHandler<ResourceKey<Item>> INVENTORY_CHANGED = GROUP.common("inventoryChanged", ItemEvents.SUPPORTS_ITEM, () -> InventoryChangedKubeEvent.class);
	SpecializedEventHandler<ResourceKey<MenuType<?>>> CHEST_OPENED = GROUP.common("chestOpened", SUPPORTS_MENU_TYPE, () -> ChestKubeEvent.class);
	SpecializedEventHandler<ResourceKey<MenuType<?>>> CHEST_CLOSED = GROUP.common("chestClosed", SUPPORTS_MENU_TYPE, () -> ChestKubeEvent.class);
	SpecializedEventHandler<String> STAGE_ADDED = GROUP.common("stageAdded", Extra.STRING, () -> StageChangedEvent.class);
	SpecializedEventHandler<String> STAGE_REMOVED = GROUP.common("stageRemoved", Extra.STRING, () -> StageChangedEvent.class);
}
