package dev.latvian.kubejs.player;

import dev.latvian.kubejs.entity.LivingEntityEventJS;
import dev.latvian.kubejs.server.ServerJS;
import net.minecraft.entity.Entity;

/**
 * @author LatvianModder
 */
public class PlayerEventJS extends LivingEntityEventJS
{
	public final PlayerJS player;

	public PlayerEventJS(Entity p)
	{
		super(p);
		player = ServerJS.instance.player(p.getUniqueID());
	}
}