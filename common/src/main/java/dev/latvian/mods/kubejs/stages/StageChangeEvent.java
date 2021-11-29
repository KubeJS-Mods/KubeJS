package dev.latvian.mods.kubejs.stages;

import net.minecraft.world.entity.player.Player;

public class StageChangeEvent {
	private final Stages stages;
	private final String stage;

	StageChangeEvent(Stages p, String s) {
		stages = p;
		stage = s;
	}

	public Player getPlayer() {
		return stages.player;
	}

	public String getStage() {
		return stage;
	}

	public Stages getPlayerStages() {
		return stages;
	}
}
