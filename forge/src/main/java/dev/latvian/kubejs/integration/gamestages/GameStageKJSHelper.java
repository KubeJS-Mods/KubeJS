package dev.latvian.kubejs.integration.gamestages;

import net.darkhax.gamestages.GameStageHelper;
import net.darkhax.gamestages.data.IStageData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

/**
 * @author LatvianModder
 */
public class GameStageKJSHelper
{
	public static boolean hasStage(Player player, String stage)
	{
		IStageData data = GameStageHelper.getPlayerData(player);

		if (player instanceof ServerPlayer)
		{
			return GameStageHelper.hasStage(player, data, stage);
		}

		return data != null && data.hasStage(stage);
	}

	public static void addStage(ServerPlayer player, String stage)
	{
		GameStageHelper.addStage(player, stage);
		GameStageHelper.syncPlayer(player);
	}

	public static void removeStage(ServerPlayer player, String stage)
	{
		GameStageHelper.removeStage(player, stage);
		GameStageHelper.syncPlayer(player);
	}
}