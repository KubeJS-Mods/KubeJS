package dev.latvian.mods.kubejs.player;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.ChatEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.TickEvent;
import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.bindings.event.PlayerEvents;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.advancements.Advancement;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.InventoryMenu;

/**
 * @author LatvianModder
 */
public class KubeJSPlayerEventHandler {
	public static void init() {
		PlayerEvent.PLAYER_JOIN.register(KubeJSPlayerEventHandler::loggedIn);
		PlayerEvent.PLAYER_QUIT.register(KubeJSPlayerEventHandler::loggedOut);
		TickEvent.PLAYER_POST.register(KubeJSPlayerEventHandler::tick);
		ChatEvent.DECORATE.register(KubeJSPlayerEventHandler::chatDecorate);
		ChatEvent.RECEIVED.register(KubeJSPlayerEventHandler::chatReceived);
		PlayerEvent.PLAYER_ADVANCEMENT.register(KubeJSPlayerEventHandler::advancement);
		PlayerEvent.OPEN_MENU.register(KubeJSPlayerEventHandler::inventoryOpened);
		PlayerEvent.CLOSE_MENU.register(KubeJSPlayerEventHandler::inventoryClosed);
	}

	public static void loggedIn(ServerPlayer player) {
		PlayerEvents.LOGGED_IN.post(new SimplePlayerEventJS(player));
		player.inventoryMenu.addSlotListener(player.kjs$getInventoryChangeListener());

		if (!ScriptType.SERVER.errors.isEmpty() && !CommonProperties.get().hideServerScriptErrors) {
			player.displayClientMessage(ScriptType.SERVER.errorsComponent("/kubejs errors"), false);
		}

		player.kjs$getStages().sync();
	}

	public static void respawn(ServerPlayer oldPlayer, ServerPlayer newPlayer, boolean keepData) {
		newPlayer.kjs$setRawPersistentData(oldPlayer.kjs$getRawPersistentData());
		newPlayer.inventoryMenu.addSlotListener(newPlayer.kjs$getInventoryChangeListener());
		PlayerEvents.RESPAWNED.post(new PlayerRespawnedEventJS(newPlayer, oldPlayer, keepData));
		newPlayer.kjs$getStages().sync();
	}

	public static void loggedOut(ServerPlayer player) {
		PlayerEvents.LOGGED_OUT.post(new SimplePlayerEventJS(player));
	}

	public static void tick(Player player) {
		if (player instanceof ServerPlayer) {
			PlayerEvents.TICK.post(new SimplePlayerEventJS(player));
		}
	}

	public static void chatDecorate(ServerPlayer player, ChatEvent.ChatComponent component) {
		PlayerEvents.DECORATE_CHAT.post(new PlayerChatDecorateEventJS(player, component));
	}

	public static EventResult chatReceived(ServerPlayer player, Component component) {
		var event = new PlayerChatReceivedEventJS(player, component);
		return PlayerEvents.CHAT.post(event) ? EventResult.interruptFalse() : EventResult.pass();
	}

	public static void advancement(ServerPlayer player, Advancement advancement) {
		PlayerEvents.ADVANCEMENT.post(String.valueOf(advancement.getId()), new PlayerAdvancementEventJS(player, advancement));
	}

	public static void inventoryOpened(Player player, AbstractContainerMenu menu) {
		if (player instanceof ServerPlayer serverPlayer) {
			if (!(menu instanceof InventoryMenu)) {
				menu.addSlotListener(serverPlayer.kjs$getInventoryChangeListener());
			}

			PlayerEvents.INVENTORY_OPENED.post(new InventoryEventJS(serverPlayer, menu));

			if (menu instanceof ChestMenu) {
				PlayerEvents.CHEST_OPENED.post(new ChestEventJS(serverPlayer, menu));
			}
		}
	}

	public static void inventoryClosed(Player player, AbstractContainerMenu menu) {
		if (player instanceof ServerPlayer serverPlayer) {
			PlayerEvents.INVENTORY_CLOSED.post(new InventoryEventJS(serverPlayer, menu));

			if (menu instanceof ChestMenu) {
				PlayerEvents.CHEST_CLOSED.post(new ChestEventJS(serverPlayer, menu));
			}
		}
	}
}