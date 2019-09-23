package dev.latvian.kubejs.player;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.entity.LivingEntityEventJS;
import dev.latvian.kubejs.integration.gamestages.GameStagesIntegration;
import net.minecraftforge.fml.common.Loader;

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
		if (getPlayer() != null && Loader.isModLoaded("gamestages"))
		{
			return GameStagesIntegration.hasStage(getPlayer().getPlayerEntity(), stage);
		}

		return false;
	}

	public void addGameStage(String stage)
	{
		if (Loader.isModLoaded("gamestages"))
		{
			if (getPlayer() != null)
			{
				GameStagesIntegration.addStage(getPlayer().getPlayerEntity(), stage);
			}
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
			if (getPlayer() != null)
			{
				GameStagesIntegration.removeStage(getPlayer().getPlayerEntity(), stage);
			}
		}
		else
		{
			KubeJS.LOGGER.error("Can't remove gamestage " + stage + ", GameStages mod isn't loaded!");
		}
	}
}