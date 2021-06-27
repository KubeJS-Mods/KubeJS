package dev.latvian.kubejs.integration.gamestages;

import net.darkhax.gamestages.data.GameStageSaveHandler;
import net.darkhax.gamestages.data.IStageData;

/**
 * @author LatvianModder
 */
public class GameStageClientHelper {
	public static void setClientData(IStageData stageData) {
		GameStageSaveHandler.setClientData(stageData);
	}
}