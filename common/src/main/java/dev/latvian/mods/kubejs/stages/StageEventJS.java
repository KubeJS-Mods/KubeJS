package dev.latvian.mods.kubejs.stages;

import dev.latvian.mods.kubejs.player.PlayerEventJS;
import net.minecraft.world.entity.player.Player;

/**
 * @author LatvianModder
 */
public class StageEventJS extends PlayerEventJS {
	private final StageChangeEvent event;

	public StageEventJS(StageChangeEvent e) {
		event = e;
	}

	public Stages getPlayerStages() {
		return event.getPlayerStages();
	}

	@Override
	public Player getEntity() {
		return event.getPlayer();
	}

	public String getStage() {
		return event.getStage();
	}
}