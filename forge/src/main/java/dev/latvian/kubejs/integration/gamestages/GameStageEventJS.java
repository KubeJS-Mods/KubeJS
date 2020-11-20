package dev.latvian.kubejs.integration.gamestages;

import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.player.PlayerEventJS;
import net.darkhax.gamestages.event.GameStageEvent;

/**
 * @author LatvianModder
 */
public class GameStageEventJS extends PlayerEventJS
{
	private final GameStageEvent event;

	public GameStageEventJS(GameStageEvent e)
	{
		event = e;
	}

	@Override
	public EntityJS getEntity()
	{
		return entityOf(event.getEntity());
	}

	public String getStage()
	{
		return event.getStageName();
	}
}