package dev.latvian.mods.kubejs.client;

import dev.latvian.mods.kubejs.player.KubePlayerEvent;
import net.minecraft.client.player.LocalPlayer;

public class ClientKubeEvent implements KubePlayerEvent {
	private final LocalPlayer player;

	public ClientKubeEvent(LocalPlayer player) {
		this.player = player;
	}

	@Override
	public LocalPlayer getEntity() {
		return player;
	}

	@Override
	public LocalPlayer getPlayer() {
		return player;
	}
}
