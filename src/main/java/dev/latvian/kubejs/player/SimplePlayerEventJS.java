package dev.latvian.kubejs.player;

import dev.latvian.kubejs.entity.EntityJS;
import net.minecraft.entity.player.PlayerEntity;

/**
 * @author LatvianModder
 */
public class SimplePlayerEventJS extends PlayerEventJS
{
	private final PlayerEntity player;

	public SimplePlayerEventJS(PlayerEntity p)
	{
		player = p;
	}

	@Override
	public EntityJS getEntity()
	{
		return entityOf(player);
	}
}