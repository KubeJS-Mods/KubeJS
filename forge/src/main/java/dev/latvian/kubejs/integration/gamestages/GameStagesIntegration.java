package dev.latvian.kubejs.integration.gamestages;

import dev.latvian.kubejs.player.AttachPlayerDataEvent;
import net.darkhax.gamestages.event.GameStageEvent;
import net.minecraftforge.common.MinecraftForge;

/**
 * @author LatvianModder
 */
public class GameStagesIntegration
{
	public void init()
	{
		AttachPlayerDataEvent.EVENT.register(this::attachPlayerData);
		MinecraftForge.EVENT_BUS.addListener(this::gameStageAdded);
		MinecraftForge.EVENT_BUS.addListener(this::gameStageRemoved);
	}

	private void attachPlayerData(AttachPlayerDataEvent event)
	{
		event.add("gamestages", new GameStagesPlayerData(event.getParent()));
	}

	private void gameStageAdded(GameStageEvent.Added e)
	{
		new GameStageEventJS(e).post("gamestage.added", e.getStageName());
	}

	private void gameStageRemoved(GameStageEvent.Removed e)
	{
		new GameStageEventJS(e).post("gamestage.removed", e.getStageName());
	}
}