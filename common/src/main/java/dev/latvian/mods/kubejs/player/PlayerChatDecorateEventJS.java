package dev.latvian.mods.kubejs.player;

import dev.architectury.event.events.common.ChatEvent;
import dev.latvian.mods.kubejs.typings.JsInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

@JsInfo("""
		Invoked when a player sends a chat message.
				
		If cancelled (`PlayerEvents.chat`), the message will not be sent.
		""")
public class PlayerChatDecorateEventJS extends PlayerEventJS {
	private final ServerPlayer player;
	public ChatEvent.ChatComponent chatComponent;

	public PlayerChatDecorateEventJS(ServerPlayer player, ChatEvent.ChatComponent chatComponent) {
		this.player = player;
		this.chatComponent = chatComponent;
	}

	@Override
	@JsInfo("Gets the player that sent the message.")
	public ServerPlayer getEntity() {
		return player;
	}

	@JsInfo("Gets the username of the player that sent the message.")
	public String getUsername() {
		return player.getGameProfile().getName();
	}

	@JsInfo("Gets the message that the player sent.")
	public String getMessage() {
		return chatComponent.get().getString();
	}

	@JsInfo("Gets the message that the player sent.")
	public Component getComponent() {
		return chatComponent.get();
	}

	@JsInfo("Sets the message that the player sent.")
	public void setMessage(Component text) {
		chatComponent.set(text);
	}

	@JsInfo("Sets the message that the player sent.")
	public void setComponent(Component text) {
		chatComponent.set(text);
	}
}