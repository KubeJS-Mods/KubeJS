package dev.latvian.kubejs.world;

import dev.latvian.kubejs.event.EventJS;
import dev.latvian.kubejs.server.ServerJS;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public abstract class WorldEventJS extends EventJS
{
	public abstract WorldJS getWorld();

	@Nullable
	public ServerJS getServer()
	{
		return getWorld().getServer();
	}

	protected WorldJS worldOf(World world)
	{
		return UtilsJS.getWorld(world);
	}

	protected WorldJS worldOf(Entity entity)
	{
		return worldOf(entity.world);
	}

	public final boolean post(String id)
	{
		return post(getWorld().getSide(), id);
	}

	public final boolean post(String id, String sub)
	{
		return post(getWorld().getSide(), id, sub);
	}
}