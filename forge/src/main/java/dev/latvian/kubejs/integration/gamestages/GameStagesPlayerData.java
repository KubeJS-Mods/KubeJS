package dev.latvian.kubejs.integration.gamestages;

import dev.latvian.kubejs.player.PlayerDataJS;
import net.darkhax.gamestages.GameStageHelper;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author LatvianModder
 */
public class GameStagesPlayerData
{
	private final PlayerDataJS playerData;

	public GameStagesPlayerData(PlayerDataJS d)
	{
		playerData = d;
	}

	public boolean has(String stage)
	{
		return GameStageKJSHelper.hasStage(playerData.getMinecraftPlayer(), stage);
	}

	public void add(String stage)
	{
		if (playerData.getMinecraftPlayer() instanceof ServerPlayer)
		{
			GameStageHelper.addStage((ServerPlayer) playerData.getMinecraftPlayer(), stage);
		}
	}

	public void remove(String stage)
	{
		if (playerData.getMinecraftPlayer() instanceof ServerPlayer)
		{
			GameStageHelper.removeStage((ServerPlayer) playerData.getMinecraftPlayer(), stage);
		}
	}

	public boolean set(String stage, boolean value)
	{
		if (value)
		{
			add(stage);
			return true;
		}
		else
		{
			remove(stage);
			return false;
		}
	}

	public boolean toggle(String stage)
	{
		return set(stage, !has(stage));
	}

	public Collection<String> getList()
	{
		return GameStageHelper.getPlayerData(playerData.getMinecraftPlayer()).getStages();
	}

	public void clear()
	{
		for (String s : new ArrayList<>(getList()))
		{
			remove(s);
		}
	}

	public void sync()
	{
		if (playerData.getMinecraftPlayer() instanceof ServerPlayer)
		{
			GameStageHelper.syncPlayer((ServerPlayer) playerData.getMinecraftPlayer());
		}
	}
}