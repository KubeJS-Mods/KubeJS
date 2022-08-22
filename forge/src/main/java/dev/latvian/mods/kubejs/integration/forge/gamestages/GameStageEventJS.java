package dev.latvian.mods.kubejs.integration.forge.gamestages;

import dev.latvian.mods.kubejs.entity.EntityJS;
import dev.latvian.mods.kubejs.player.PlayerEventJS;
import net.darkhax.gamestages.event.GameStageEvent;

/**
 * @author LatvianModder
 */
public class GameStageEventJS extends PlayerEventJS {
	private final GameStageEvent event;

	public GameStageEventJS(GameStageEvent e) {
		event = e;
	}

	@Override
	public EntityJS getEntity() {
		return entityOf(event.getEntity());
	}

	public String getStage() {
		return event.getStageName();
	}
}