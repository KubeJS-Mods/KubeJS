package dev.latvian.mods.kubejs.client;

import dev.latvian.mods.kubejs.player.PlayerEventJS;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

public class ClientEventJS extends PlayerEventJS {
	@Override
	public LocalPlayer getEntity() {
		return Minecraft.getInstance().player;
	}

	@Override
	public LocalPlayer getPlayer() {
		return Minecraft.getInstance().player;
	}
}
