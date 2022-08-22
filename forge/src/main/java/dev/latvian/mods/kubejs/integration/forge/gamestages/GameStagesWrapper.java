package dev.latvian.mods.kubejs.integration.forge.gamestages;

import dev.latvian.mods.kubejs.stages.Stages;
import net.darkhax.gamestages.GameStageHelper;
import net.darkhax.gamestages.data.IStageData;
import net.darkhax.gamestages.data.StageData;
import net.minecraft.world.entity.player.Player;

import java.util.Collection;
import java.util.Collections;

public class GameStagesWrapper extends Stages {
	public GameStagesWrapper(Player p) {
		super(p);
	}

	@Override
	public boolean addNoUpdate(String stage) {
		IStageData stageData = GameStageHelper.getPlayerData(player);

		if (stageData != null && !stageData.hasStage(stage)) {
			stageData.addStage(stage);
			return true;
		}

		return false;
	}

	@Override
	public boolean removeNoUpdate(String stage) {
		IStageData stageData = GameStageHelper.getPlayerData(player);

		if (stageData != null && stageData.hasStage(stage)) {
			stageData.removeStage(stage);
			return true;
		}

		return false;
	}

	@Override
	public Collection<String> getAll() {
		IStageData stageData = GameStageHelper.getPlayerData(player);

		return stageData != null ? stageData.getStages() : Collections.emptyList();
	}

	@Override
	public void replace(Collection<String> stages) {
		IStageData stageData = new StageData();

		for (String s : stages) {
			stageData.addStage(s);
		}

		setClientData(stageData);
	}

	private void setClientData(IStageData stageData) {
		GameStageClientHelper.setClientData(stageData);
	}
}