package dev.latvian.kubejs.player.forge;

import dev.latvian.kubejs.integration.gamestages.GameStageKJSHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class PlayerEventJSImpl
{
	public static boolean hasStage(Player player, String stage)
	{
		return GameStageKJSHelper.hasStage(player, stage);
	}

	public static void addStage(ServerPlayer player, String stage)
	{
		GameStageKJSHelper.addStage(player, stage);
	}

	public static void removeStage(ServerPlayer player, String stage)
	{
		GameStageKJSHelper.removeStage(player, stage);
	}
}
