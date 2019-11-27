package dev.latvian.kubejs.player;

import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.entity.LivingEntityEventJS;
import dev.latvian.kubejs.integration.gamestages.GameStageKJSHelper;
import net.minecraftforge.fml.ModList;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public abstract class PlayerEventJS extends LivingEntityEventJS
{
	@Nullable
	public PlayerJS getPlayer()
	{
		EntityJS e = getEntity();

		if (e instanceof PlayerJS)
		{
			return (PlayerJS) e;
		}

		return null;
	}

	// Helper methods for Game Stages

	public boolean hasGameStage(String stage)
	{
		if (getPlayer() != null && ModList.get().isLoaded("gamestages"))
		{
			return GameStageKJSHelper.hasStage(getPlayer().minecraftPlayer, stage);
		}

		return false;
	}

	public void addGameStage(String stage)
	{
		if (ModList.get().isLoaded("gamestages"))
		{
			if (getPlayer() != null)
			{
				GameStageKJSHelper.addStage(getPlayer().minecraftPlayer, stage);
			}
		}
		else
		{
			getWorld().getSide().console.error("Can't add gamestage " + stage + ", GameStages mod isn't loaded!");
		}
	}

	public void removeGameStage(String stage)
	{
		if (ModList.get().isLoaded("gamestages"))
		{
			if (getPlayer() != null)
			{
				GameStageKJSHelper.removeStage(getPlayer().minecraftPlayer, stage);
			}
		}
		else
		{
			getWorld().getSide().console.error("Can't remove gamestage " + stage + ", GameStages mod isn't loaded!");
		}
	}
}