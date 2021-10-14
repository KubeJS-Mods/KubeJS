package dev.latvian.kubejs.player;

import dev.latvian.kubejs.CommonProperties;
import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.server.ServerJS;
import dev.latvian.kubejs.stages.Stages;
import dev.architectury.architectury.event.events.ChatEvent;
import dev.architectury.architectury.event.events.PlayerEvent;
import dev.architectury.architectury.event.events.TickEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.Advancement;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResultHolder;
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
			ServerPlayerDataJS p = new ServerPlayerDataJS(ServerJS.instance, player.getUUID(), player.getGameProfile().getName(), KubeJS.nextClientHasClientMod);
			KubeJS.nextClientHasClientMod = false;
			p.getServer().playerMap.put(p.getId(), p);
			new AttachPlayerDataEvent(p).invoke();
			new SimplePlayerEventJS(player).post(KubeJSEvents.PLAYER_LOGGED_IN);
			player.inventoryMenu.addSlotListener(new InventoryListener(player));
		}

		if (!ScriptType.SERVER.errors.isEmpty() && !CommonProperties.get().hideServerScriptErrors) {
			player.displayClientMessage(new TextComponent("KubeJS errors found [" + ScriptType.SERVER.errors.size() + "]! Run '/kubejs errors' for more info").withStyle(ChatFormatting.DARK_RED), false);
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

		new SimplePlayerEventJS(player).post(KubeJSEvents.PLAYER_LOGGED_OUT);
		ServerJS.instance.playerMap.remove(player.getUUID());
	}

	public static void cloned(ServerPlayer oldPlayer, ServerPlayer newPlayer, boolean wonGame) {
		newPlayer.inventoryMenu.addSlotListener(new InventoryListener(newPlayer));
	}

	public static void tick(Player player) {
		if (ServerJS.instance != null && player instanceof ServerPlayer) {
			new SimplePlayerEventJS(player).post(KubeJSEvents.PLAYER_TICK);
		}
	}

	@NotNull
	public static InteractionResultHolder<Component> chat(ServerPlayer player, String message, Component component) {
		PlayerChatEventJS event = new PlayerChatEventJS(player, message, component);
		if (event.post(KubeJSEvents.PLAYER_CHAT)) {
			return InteractionResultHolder.fail(event.component);
		}
		return InteractionResultHolder.pass(event.component);
	}

	public static void advancement(ServerPlayer player, Advancement advancement) {
		new PlayerAdvancementEventJS(player, advancement).post(KubeJSEvents.PLAYER_ADVANCEMENT);
	}

	public static void inventoryOpened(Player player, AbstractContainerMenu menu) {
		if (player instanceof ServerPlayer && !(menu instanceof InventoryMenu)) {
			menu.addSlotListener(new InventoryListener((ServerPlayer) player));
		}

		new InventoryEventJS(player, menu).post(KubeJSEvents.PLAYER_INVENTORY_OPENED);

		if (menu instanceof ChestMenu) {
			new ChestEventJS(player, menu).post(KubeJSEvents.PLAYER_CHEST_OPENED);
		}
	}

	public static void inventoryClosed(Player player, AbstractContainerMenu menu) {
		new InventoryEventJS(player, menu).post(KubeJSEvents.PLAYER_INVENTORY_CLOSED);

		if (menu instanceof ChestMenu) {
			new ChestEventJS(player, menu).post(KubeJSEvents.PLAYER_CHEST_CLOSED);
		}
	}
}