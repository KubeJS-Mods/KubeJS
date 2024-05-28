package dev.latvian.mods.kubejs.player;

import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.server.level.ServerPlayer;

@Info("""
	Invoked when a player gets an advancement.
	""")
public class PlayerAdvancementKubeEvent implements KubePlayerEvent {
	private final ServerPlayer player;
	private final AdvancementNode advancementNode;

	public PlayerAdvancementKubeEvent(ServerPlayer player, AdvancementNode advancementNode) {
		this.player = player;
		this.advancementNode = advancementNode;
	}

	@Override
	@Info("Returns the player that got the advancement.")
	public ServerPlayer getEntity() {
		return player;
	}

	@Info("Returns the advancement that was obtained.")
	public AdvancementNode getAdvancement() {
		return advancementNode;
	}
}