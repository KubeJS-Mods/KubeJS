package dev.latvian.mods.kubejs.player;

import net.minecraft.server.level.ServerPlayer;

/**
 * @author LatvianModder
 */
public class PlayerClonedEventJS extends PlayerEventJS {
	private final ServerPlayer player;
	private final ServerPlayer oldPlayer;
	private final boolean wonGame;

	public PlayerClonedEventJS(ServerPlayer player, ServerPlayer oldPlayer, boolean wonGame) {
		this.player = player;
		this.oldPlayer = oldPlayer;
		this.wonGame = wonGame;
	}

	@Override
	public ServerPlayer getEntity() {
		return player;
	}

	public ServerPlayer getOldPlayer() {
		return oldPlayer;
	}

	public boolean getWonGame() {
		return wonGame;
	}
}