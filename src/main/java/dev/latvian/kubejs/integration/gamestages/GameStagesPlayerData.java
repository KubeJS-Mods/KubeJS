package dev.latvian.kubejs.integration.gamestages;

import dev.latvian.kubejs.player.PlayerDataJS;
import net.darkhax.gamestages.GameStageHelper;

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
		return GameStageHelper.hasStage(playerData.getMinecraftPlayer(), stage);
	}

	public void add(String stage)
	{
		GameStageHelper.addStage(playerData.getMinecraftPlayer(), stage);
	}

	public void remove(String stage)
	{
		GameStageHelper.removeStage(playerData.getMinecraftPlayer(), stage);
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
		GameStageHelper.syncPlayer(playerData.getMinecraftPlayer());
	}
}