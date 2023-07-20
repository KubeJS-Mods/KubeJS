package dev.latvian.mods.kubejs.player;

import dev.latvian.mods.kubejs.typings.JsInfo;
import net.minecraft.advancements.Advancement;
import net.minecraft.server.level.ServerPlayer;

@JsInfo("""
		Invoked when a player gets an advancement.
		""")
public class PlayerAdvancementEventJS extends PlayerEventJS {
	private final ServerPlayer player;
	private final Advancement advancement;

	public PlayerAdvancementEventJS(ServerPlayer player, Advancement advancement) {
		this.player = player;
		this.advancement = advancement;
	}

	@Override
	@JsInfo("Returns the player that got the advancement.")
	public ServerPlayer getEntity() {
		return player;
	}

	@JsInfo("Returns the advancement that was obtained.")
	public AdvancementJS getAdvancement() {
		return new AdvancementJS(advancement);
	}
}