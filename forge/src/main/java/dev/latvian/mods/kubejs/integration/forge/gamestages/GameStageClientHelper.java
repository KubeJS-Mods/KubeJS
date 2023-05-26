package dev.latvian.mods.kubejs.integration.forge.gamestages;

import net.darkhax.gamestages.data.GameStageSaveHandler;
import net.darkhax.gamestages.data.IStageData;

public class GameStageClientHelper {
	public static void setClientData(IStageData stageData) {
		GameStageSaveHandler.setClientData(stageData);
	}
}