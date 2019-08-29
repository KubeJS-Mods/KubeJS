package dev.latvian.kubejs.world;

import dev.latvian.kubejs.server.ServerEventJS;

/**
 * @author LatvianModder
 */
public class WorldEventJS extends ServerEventJS
{
	public final WorldJS world;

	public WorldEventJS(WorldJS w)
	{
		super(w.server);
		world = w;
	}
}