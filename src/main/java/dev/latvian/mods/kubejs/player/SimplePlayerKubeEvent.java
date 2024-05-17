package dev.latvian.mods.kubejs.player;

import net.minecraft.world.entity.player.Player;

public class SimplePlayerKubeEvent implements KubePlayerEvent {
	private final Player player;

	public SimplePlayerKubeEvent(Player p) {
		player = p;
	}

	@Override
	public Player getEntity() {
		return player;
	}
}