package dev.latvian.kubejs.integration.gamestages;

import net.darkhax.gamestages.GameStageHelper;
import net.darkhax.gamestages.data.IStageData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;

/**
 * @author LatvianModder
 */
public class GameStageKJSHelper
{
	public static boolean hasStage(PlayerEntity player, String stage)
	{
		IStageData data = GameStageHelper.getPlayerData(player);

		if (player instanceof ServerPlayerEntity)
		{
			return GameStageHelper.hasStage((ServerPlayerEntity) player, data, stage);
		}

		return data != null && data.hasStage(stage);
	}

	public static void addStage(ServerPlayerEntity player, String stage)
	{
		GameStageHelper.addStage(player, stage);
		GameStageHelper.syncPlayer(player);
	}

	public static void removeStage(ServerPlayerEntity player, String stage)
	{
		GameStageHelper.removeStage(player, stage);
		GameStageHelper.syncPlayer(player);
	}
}