package dev.latvian.kubejs.integration.gamestages;

import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.player.PlayerEventJS;
import net.minecraft.entity.player.EntityPlayer;

/**
 * @author LatvianModder
 */
public class GameStageEventJS extends PlayerEventJS
{
	private final EntityPlayer player;
	private final String stage;

	public GameStageEventJS(EntityPlayer p, String s)
	{
		player = p;
		stage = s;
	}

	@Override
	public EntityJS getEntity()
	{
		return entityOf(player);
	}

	public String getStage()
	{
		return stage;
	}
}