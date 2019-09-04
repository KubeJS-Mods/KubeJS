package dev.latvian.kubejs.player;

import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.entity.LivingEntityEventJS;

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
}