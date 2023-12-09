package dev.latvian.mods.kubejs.player;

import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.server.level.ServerPlayer;

@Info("""
	Invoked when a player gets an advancement.
	""")
public class PlayerAdvancementEventJS extends PlayerEventJS {
	private final ServerPlayer player;
	private final AdvancementHolder advancement;

	public PlayerAdvancementEventJS(ServerPlayer player, AdvancementHolder advancement) {
		this.player = player;
		this.advancement = advancement;
	}

	@Override
	@Info("Returns the player that got the advancement.")
	public ServerPlayer getEntity() {
		return player;
	}

	@Info("Returns the advancement that was obtained.")
	public AdvancementJS getAdvancement() {
		return new AdvancementJS(advancement);
	}
}