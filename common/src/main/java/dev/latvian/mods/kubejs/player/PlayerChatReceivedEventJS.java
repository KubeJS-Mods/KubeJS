package dev.latvian.mods.kubejs.player;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;

/**
 * @author LatvianModder
 */
public class PlayerChatReceivedEventJS extends PlayerEventJS {
	private final ServerPlayer player;
	public final Component component;

	public PlayerChatReceivedEventJS(ServerPlayer player, Component component) {
		this.player = player;
		this.component = component;
	}

	@Override
	public ServerPlayer getEntity() {
		return player;
	}

	public String getUsername() {
		return player.getGameProfile().getName();
	}

	public String getMessage() {
		return component.getString();
	}

	public MutableComponent getComponent() {
		return component.copy();
	}
}