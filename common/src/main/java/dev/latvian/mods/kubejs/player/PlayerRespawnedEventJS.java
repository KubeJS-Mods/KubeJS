package dev.latvian.mods.kubejs.player;

import net.minecraft.server.level.ServerPlayer;

public class PlayerRespawnedEventJS extends PlayerEventJS {
	private final ServerPlayer player;
	private final ServerPlayer oldPlayer;
	private final boolean keepData;

	public PlayerRespawnedEventJS(ServerPlayer player, ServerPlayer oldPlayer, boolean keepData) {
		this.player = player;
		this.oldPlayer = oldPlayer;
		this.keepData = keepData;
	}

	@Override
	public ServerPlayer getEntity() {
		return player;
	}

	public ServerPlayer getOldPlayer() {
		return oldPlayer;
	}

	public boolean getKeepData() {
		return keepData;
	}
}