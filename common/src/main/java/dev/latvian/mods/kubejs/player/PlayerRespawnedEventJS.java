package dev.latvian.mods.kubejs.player;

import dev.latvian.mods.kubejs.typings.JsInfo;
import net.minecraft.server.level.ServerPlayer;

@JsInfo("""
		Invoked when a player respawns.
				
		The reason of respawn can be either death or returning from the end.
		""")
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
	@JsInfo("Gets the player that respawned.")
	public ServerPlayer getEntity() {
		return player;
	}

	@JsInfo("Gets the player that was before respawn. Note that this entity is already removed from the world.")
	public ServerPlayer getOldPlayer() {
		return oldPlayer;
	}

	@JsInfo("Gets whether the player's data was kept, e.g. when returning from the end.")
	public boolean getKeepData() {
		return keepData;
	}
}