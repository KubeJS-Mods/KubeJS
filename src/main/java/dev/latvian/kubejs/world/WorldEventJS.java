package dev.latvian.kubejs.world;

import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.server.ServerJS;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

/**
 * @author LatvianModder
 */
public abstract class WorldEventJS extends EventJS
{
	public abstract WorldJS getWorld();

	public ServerJS getServer()
	{
		WorldJS w = getWorld();

		if (w instanceof ServerWorldJS)
		{
			return ((ServerWorldJS) w).getServer();
		}

		throw new IllegalStateException("Can't access server on client side!");
	}

	protected WorldJS worldOf(World world)
	{
		return UtilsJS.getWorld(world);
	}

	protected WorldJS worldOf(Entity entity)
	{
		return worldOf(entity.world);
	}
}