package dev.latvian.kubejs.world;

import dev.latvian.kubejs.event.EventJS;

/**
 * @author LatvianModder
 */
public class WorldEventJS extends EventJS
{
	public final WorldJS world;

	public WorldEventJS(WorldJS w)
	{
		world = w;
	}
}