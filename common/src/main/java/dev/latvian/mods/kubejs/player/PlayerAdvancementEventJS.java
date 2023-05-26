package dev.latvian.mods.kubejs.player;

import net.minecraft.advancements.Advancement;
import net.minecraft.server.level.ServerPlayer;

public class PlayerAdvancementEventJS extends PlayerEventJS {
	private final ServerPlayer player;
	private final Advancement advancement;

	public PlayerAdvancementEventJS(ServerPlayer player, Advancement advancement) {
		this.player = player;
		this.advancement = advancement;
	}

	@Override
	public ServerPlayer getEntity() {
		return player;
	}

	public AdvancementJS getAdvancement() {
		return new AdvancementJS(advancement);
	}
}