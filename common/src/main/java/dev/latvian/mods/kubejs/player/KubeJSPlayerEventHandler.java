package dev.latvian.mods.kubejs.player;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.ChatEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.TickEvent;
import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSEvents;
import dev.latvian.mods.kubejs.script.AttachDataEvent;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.server.ServerJS;
import dev.latvian.mods.kubejs.stages.Stages;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.Advancement;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.InventoryMenu;
import org.jetbrains.annotations.NotNull;

/**
 * @author LatvianModder
 */
public class KubeJSPlayerEventHandler {
	public static void init() {
		PlayerEvent.PLAYER_JOIN.register(KubeJSPlayerEventHandler::loggedIn);
		PlayerEvent.PLAYER_QUIT.register(KubeJSPlayerEventHandler::loggedOut);
		PlayerEvent.PLAYER_RESPAWN.register(KubeJSPlayerEventHandler::respawn);
		PlayerEvent.PLAYER_CLONE.register(KubeJSPlayerEventHandler::cloned);
		TickEvent.PLAYER_POST.register(KubeJSPlayerEventHandler::tick);
		ChatEvent.SERVER.register(KubeJSPlayerEventHandler::chat);
		PlayerEvent.PLAYER_ADVANCEMENT.register(KubeJSPlayerEventHandler::advancement);
		PlayerEvent.OPEN_MENU.register(KubeJSPlayerEventHandler::inventoryOpened);
		PlayerEvent.CLOSE_MENU.register(KubeJSPlayerEventHandler::inventoryClosed);
	}

	public static void loggedIn(ServerPlayer player) {
		if (ServerJS.instance != null) {
			var p = new ServerPlayerDataJS(ServerJS.instance, player.getUUID(), player.getGameProfile().getName(), KubeJS.nextClientHasClientMod);
			KubeJS.nextClientHasClientMod = false;
			p.getServer().playerMap.put(p.getId(), p);
			AttachDataEvent.forPlayer(p).invoke();
			SimplePlayerEventJS.LOGGED_IN_EVENT.post(new SimplePlayerEventJS(player));
			player.inventoryMenu.addSlotListener(new InventoryListener(player));
		}

		if (!ScriptType.SERVER.errors.isEmpty() && !CommonProperties.get().hideServerScriptErrors) {
			player.displayClientMessage(Component.literal("KubeJS errors found [" + ScriptType.SERVER.errors.size() + "]! Run ")
							.append(Component.literal("'/kubejs errors'")
									.click(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/kubejs errors"))
									.hover(Component.literal("Click to run")))
							.append(Component.literal(" for more info"))
							.withStyle(ChatFormatting.DARK_RED),
					false);
		}

		Stages.get(player).sync();
	}

	private static void respawn(ServerPlayer player, boolean b) {
		Stages.get(player).sync();
	}

	public static void loggedOut(ServerPlayer player) {
		if (ServerJS.instance == null || !ServerJS.instance.playerMap.containsKey(player.getUUID())) {
			return;
		}

		SimplePlayerEventJS.LOGGED_OUT_EVENT.post(new SimplePlayerEventJS(player));
		ServerJS.instance.playerMap.remove(player.getUUID());
	}

	public static void cloned(ServerPlayer oldPlayer, ServerPlayer newPlayer, boolean wonGame) {
		newPlayer.getPersistentDataKJS().merge(oldPlayer.getPersistentDataKJS());
		newPlayer.inventoryMenu.addSlotListener(new InventoryListener(newPlayer));
	}

	public static void tick(Player player) {
		if (ServerJS.instance != null && player instanceof ServerPlayer) {
			SimplePlayerEventJS.TICK_EVENT.post(new SimplePlayerEventJS(player));
		}
	}

	@NotNull
	public static EventResult chat(ServerPlayer player, ChatEvent.ChatComponent component) {
		var event = new PlayerChatEventJS(player, component.getRaw());
		if (PlayerChatEventJS.EVENT.post(event)) {
			return EventResult.interruptFalse();
		}
		component.setRaw(event.component);
		return EventResult.pass();
	}

	public static void advancement(ServerPlayer player, Advancement advancement) {
		new PlayerAdvancementEventJS(player, advancement).post(KubeJSEvents.PLAYER_ADVANCEMENT);
	}

	public static void inventoryOpened(Player player, AbstractContainerMenu menu) {
		if (player instanceof ServerPlayer serverPlayer) {
			if (!(menu instanceof InventoryMenu)) {
				menu.addSlotListener(new InventoryListener(serverPlayer));
			}

			InventoryEventJS.OPENED_EVENT.post(new InventoryEventJS(serverPlayer, menu));

			if (menu instanceof ChestMenu) {
				ChestEventJS.CHEST_OPENED_EVENT.post(new ChestEventJS(serverPlayer, menu));
			}
		}
	}

	public static void inventoryClosed(Player player, AbstractContainerMenu menu) {
		if (player instanceof ServerPlayer serverPlayer) {
			InventoryEventJS.CLOSED_EVENT.post(new InventoryEventJS(serverPlayer, menu));

			if (menu instanceof ChestMenu) {
				ChestEventJS.CHEST_CLOSED_EVENT.post(new ChestEventJS(serverPlayer, menu));
			}
		}
	}
}