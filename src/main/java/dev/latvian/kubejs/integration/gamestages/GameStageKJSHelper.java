package dev.latvian.kubejs.integration.gamestages;

import net.darkhax.gamestages.GameStageHelper;
import net.minecraft.entity.player.PlayerEntity;

/**
 * @author LatvianModder
 */
public class GameStageKJSHelper
{
	public static boolean hasStage(PlayerEntity player, String stage)
	{
		return GameStageHelper.hasStage(player, stage);
	}

	public static void addStage(PlayerEntity player, String stage)
	{
		GameStageHelper.addStage(player, stage);
		GameStageHelper.syncPlayer(player);
	}

	public static void removeStage(PlayerEntity player, String stage)
	{
		GameStageHelper.removeStage(player, stage);
		GameStageHelper.syncPlayer(player);
	}
}