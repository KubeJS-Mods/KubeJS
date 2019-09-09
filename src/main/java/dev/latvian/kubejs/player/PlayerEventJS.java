package dev.latvian.kubejs.player;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.entity.LivingEntityEventJS;
import dev.latvian.kubejs.integration.gamestages.GameStagesIntegration;
import net.minecraftforge.fml.common.Loader;

/**
 * @author LatvianModder
 */
public abstract class PlayerEventJS extends LivingEntityEventJS
{
	public PlayerJS getPlayer()
	{
		EntityJS e = getEntity();

		if (e instanceof PlayerJS)
		{
			return (PlayerJS) e;
		}

		throw new IllegalStateException("Entity is not a player!");
	}

	// Helper methods for Game Stages

	public boolean hasGameStage(String stage)
	{
		if (Loader.isModLoaded("gamestages"))
		{
			return GameStagesIntegration.hasStage(getPlayer().playerEntity, stage);
		}

		return false;
	}

	public void addGameStage(String stage)
	{
		if (Loader.isModLoaded("gamestages"))
		{
			GameStagesIntegration.addStage(getPlayer().playerEntity, stage);
		}
		else
		{
			KubeJS.LOGGER.error("Can't add gamestage " + stage + ", GameStages mod isn't loaded!");
		}
	}

	public void removeGameStage(String stage)
	{
		if (Loader.isModLoaded("gamestages"))
		{
			GameStagesIntegration.removeStage(getPlayer().playerEntity, stage);
		}
		else
		{
			KubeJS.LOGGER.error("Can't remove gamestage " + stage + ", GameStages mod isn't loaded!");
		}
	}
}