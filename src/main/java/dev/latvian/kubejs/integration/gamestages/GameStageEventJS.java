package dev.latvian.kubejs.integration.gamestages;

import dev.latvian.kubejs.player.PlayerEventJS;
import net.minecraft.entity.Entity;

/**
 * @author LatvianModder
 */
public class GameStageEventJS extends PlayerEventJS
{
	public final String stage;

	public GameStageEventJS(Entity e, String s)
	{
		super(e);
		stage = s;
	}
}