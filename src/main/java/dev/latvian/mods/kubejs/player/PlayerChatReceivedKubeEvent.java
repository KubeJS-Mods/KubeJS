package dev.latvian.mods.kubejs.player;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.ServerChatEvent;

public class PlayerChatReceivedKubeEvent implements KubePlayerEvent {
	private final ServerChatEvent event;

	public PlayerChatReceivedKubeEvent(ServerChatEvent event) {
		this.event = event;
	}

	@Override
	public ServerPlayer getEntity() {
		return event.getPlayer();
	}

	public String getUsername() {
		return event.getPlayer().getGameProfile().getName();
	}

	public String getMessage() {
		return event.getRawText();
	}

	public Component getComponent() {
		return event.getMessage();
	}

	public void setComponent(Component component) {
		event.setMessage(component);
	}
}