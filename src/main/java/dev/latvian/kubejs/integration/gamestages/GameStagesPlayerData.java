package dev.latvian.kubejs.integration.gamestages;

import dev.latvian.kubejs.documentation.DocClass;
import dev.latvian.kubejs.documentation.DocMethod;
import dev.latvian.kubejs.documentation.Param;
import dev.latvian.kubejs.player.PlayerDataJS;
import net.darkhax.gamestages.GameStageHelper;

import java.util.Collection;

/**
 * @author LatvianModder
 */
@DocClass(value = "Wrapper class for Game Stages mod")
public class GameStagesPlayerData
{
	private final PlayerDataJS playerData;

	public GameStagesPlayerData(PlayerDataJS d)
	{
		playerData = d;
	}

	@DocMethod(params = @Param("stage"))
	public boolean has(String stage)
	{
		return GameStageHelper.hasStage(playerData.getPlayerEntity(), stage);
	}

	@DocMethod(params = @Param("stage"))
	public void add(String stage)
	{
		GameStageHelper.addStage(playerData.getPlayerEntity(), stage);
	}

	@DocMethod(params = @Param("stage"))
	public void remove(String stage)
	{
		GameStageHelper.removeStage(playerData.getPlayerEntity(), stage);
	}

	public Collection<String> list()
	{
		return GameStageHelper.getPlayerData(playerData.getPlayerEntity()).getStages();
	}

	@DocMethod("Sends all stages from server to client")
	public void sync()
	{
		GameStageHelper.syncPlayer(playerData.getPlayerEntity());
	}
}