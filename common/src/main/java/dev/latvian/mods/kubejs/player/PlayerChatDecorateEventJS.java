package dev.latvian.mods.kubejs.player;

import dev.architectury.event.events.common.ChatEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

/**
 * @author LatvianModder
 */
public class PlayerChatDecorateEventJS extends PlayerEventJS {
	private final ServerPlayer player;
	public ChatEvent.ChatComponent chatComponent;

	public PlayerChatDecorateEventJS(ServerPlayer player, ChatEvent.ChatComponent chatComponent) {
		this.player = player;
		this.chatComponent = chatComponent;
	}

	@Override
	public ServerPlayer getEntity() {
		return player;
	}

	public String getUsername() {
		return player.getGameProfile().getName();
	}

	public String getMessage() {
		return chatComponent.get().getString();
	}

	public Component getComponent() {
		return chatComponent.get();
	}

	public void setMessage(Component text) {
		chatComponent.set(text);
	}

	public void setComponent(Component text) {
		chatComponent.set(text);
	}
}