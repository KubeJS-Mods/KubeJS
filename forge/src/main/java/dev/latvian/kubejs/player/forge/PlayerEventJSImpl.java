package dev.latvian.kubejs.player.forge;

import dev.latvian.kubejs.integration.gamestages.GameStageKJSHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;

public class PlayerEventJSImpl
{
	public static boolean hasStage(PlayerEntity player, String stage)
	{
		return GameStageKJSHelper.hasStage(player, stage);
	}

	public static void addStage(ServerPlayerEntity player, String stage)
	{
		GameStageKJSHelper.addStage(player, stage);
	}

	public static void removeStage(ServerPlayerEntity player, String stage)
	{
		GameStageKJSHelper.removeStage(player, stage);
	}
}
