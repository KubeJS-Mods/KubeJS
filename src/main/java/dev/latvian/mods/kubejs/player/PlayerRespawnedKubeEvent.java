package dev.latvian.mods.kubejs.player;

import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.server.level.ServerPlayer;

@Info("""
	Invoked when a player respawns.
			
	The reason of respawn can be either death or returning from the end.
	""")
public class PlayerRespawnedKubeEvent implements KubePlayerEvent {
	private final ServerPlayer player;
	private final boolean endConquered;

	public PlayerRespawnedKubeEvent(ServerPlayer player, boolean endConquered) {
		this.player = player;
		this.endConquered = endConquered;
	}

	@Override
	@Info("Gets the player that respawned.")
	public ServerPlayer getEntity() {
		return player;
	}

	public boolean isEndConquered() {
		return endConquered;
	}
}