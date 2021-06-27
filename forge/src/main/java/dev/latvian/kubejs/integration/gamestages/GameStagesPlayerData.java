package dev.latvian.kubejs.integration.gamestages;

import dev.latvian.kubejs.core.PlayerKJS;
import dev.latvian.kubejs.player.PlayerDataJS;
import dev.latvian.kubejs.stages.Stages;

import java.util.Collection;

/**
 * @author LatvianModder
 */
public class GameStagesPlayerData {
	private final PlayerDataJS playerData;

	public GameStagesPlayerData(PlayerDataJS d) {
		playerData = d;
	}

	public Stages getStages() {
		return ((PlayerKJS) playerData.getMinecraftPlayer()).getStagesKJS();
	}

	public boolean has(String stage) {
		return getStages().has(stage);
	}

	public boolean add(String stage) {
		return getStages().add(stage);
	}

	public boolean remove(String stage) {
		return getStages().remove(stage);
	}

	public boolean set(String stage, boolean value) {
		return getStages().set(stage, value);
	}

	public boolean toggle(String stage) {
		return getStages().toggle(stage);
	}

	public Collection<String> getList() {
		return getStages().getAll();
	}

	public boolean clear() {
		return getStages().clear();
	}

	public void sync() {
		getStages().sync();
	}
}