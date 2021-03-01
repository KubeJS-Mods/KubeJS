package dev.latvian.kubejs.player;

import dev.latvian.kubejs.entity.EntityJS;
import net.minecraft.world.entity.player.Player;

/**
 * @author LatvianModder
 */
public class SimplePlayerEventJS extends PlayerEventJS {
	private final Player player;

	public SimplePlayerEventJS(Player p) {
		player = p;
	}

	@Override
	public EntityJS getEntity() {
		return entityOf(player);
	}
}