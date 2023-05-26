package dev.latvian.mods.kubejs.integration.forge.gamestages;

import dev.latvian.mods.kubejs.player.PlayerEventJS;
import net.darkhax.gamestages.event.GameStageEvent;
import net.minecraft.world.entity.player.Player;

public class GameStageEventJS extends PlayerEventJS {
	private final GameStageEvent event;

	public GameStageEventJS(GameStageEvent e) {
		event = e;
	}

	@Override
	public Player getEntity() {
		return event.getEntity();
	}

	public String getStage() {
		return event.getStageName();
	}
}