package com.latmod.mods.worldjs.world;

import com.latmod.mods.worldjs.events.EventJS;
import com.latmod.mods.worldjs.mod.WorldJSMod;
import net.minecraft.world.World;

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

	public WorldEventJS(World w)
	{
		world = WorldJSMod.server.getWorld(w.provider.getDimension());
	}
}