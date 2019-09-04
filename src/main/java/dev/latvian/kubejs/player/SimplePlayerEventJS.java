package dev.latvian.kubejs.player;

import dev.latvian.kubejs.entity.EntityJS;
import net.minecraft.entity.player.EntityPlayer;

/**
 * @author LatvianModder
 */
public class SimplePlayerEventJS extends PlayerEventJS
{
	private final EntityPlayer player;

	public SimplePlayerEventJS(EntityPlayer p)
	{
		player = p;
	}

	@Override
	public EntityJS getEntity()
	{
		return entityOf(player);
	}
}