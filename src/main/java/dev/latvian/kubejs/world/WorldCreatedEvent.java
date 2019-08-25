package dev.latvian.kubejs.world;

import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * @author LatvianModder
 */
public class WorldCreatedEvent extends Event
{
	private final WorldJS world;

	public WorldCreatedEvent(WorldJS w)
	{
		world = w;
	}

	public WorldJS getWorld()
	{
		return world;
	}

	public void setData(String id, Object object)
	{
		world.data.put(id, object);
	}
}