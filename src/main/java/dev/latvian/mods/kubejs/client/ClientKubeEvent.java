package dev.latvian.mods.kubejs.client;

import dev.latvian.mods.kubejs.player.KubePlayerEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

public class ClientKubeEvent implements KubePlayerEvent {
	@Override
	public LocalPlayer getEntity() {
		return Minecraft.getInstance().player;
	}

	@Override
	public LocalPlayer getPlayer() {
		return Minecraft.getInstance().player;
	}
}
