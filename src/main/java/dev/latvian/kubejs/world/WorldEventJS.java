package dev.latvian.kubejs.world;

import dev.latvian.kubejs.util.ServerJS;
import net.minecraft.world.World;

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

	public WorldEventJS(World w)
	{
		this(ServerJS.instance.world(w));
	}
}