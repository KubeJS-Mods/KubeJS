package com.latmod.mods.worldjs.world;

import com.latmod.mods.worldjs.util.ServerJS;
import net.minecraft.world.WorldServer;

/**
 * @author LatvianModder
 */
public class WorldJS
{
	public final ServerJS server;
	public final WorldServer world;
	public final int dimension;

	public WorldJS(ServerJS s, WorldServer w)
	{
		server = s;
		world = w;
		dimension = world.provider.getDimension();
	}
}