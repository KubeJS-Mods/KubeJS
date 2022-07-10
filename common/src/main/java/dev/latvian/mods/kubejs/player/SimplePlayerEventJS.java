package dev.latvian.mods.kubejs.player;

import dev.latvian.mods.kubejs.entity.EntityJS;
import dev.latvian.mods.kubejs.event.EventHandler;
import net.minecraft.world.entity.player.Player;

/**
 * @author LatvianModder
 */
public class SimplePlayerEventJS extends PlayerEventJS {
	public static final EventHandler LOGGED_IN_EVENT = EventHandler.server(SimplePlayerEventJS.class).legacy("player.logged_in");
	public static final EventHandler LOGGED_OUT_EVENT = EventHandler.server(SimplePlayerEventJS.class).legacy("player.logged_out");
	public static final EventHandler TICK_EVENT = EventHandler.server(SimplePlayerEventJS.class).legacy("player.tick");

	private final Player player;

	public SimplePlayerEventJS(Player p) {
		player = p;
	}

	@Override
	public EntityJS getEntity() {
		return entityOf(player);
	}
}