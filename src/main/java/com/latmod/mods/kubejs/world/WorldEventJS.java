package com.latmod.mods.kubejs.world;

import com.latmod.mods.kubejs.KubeJS;
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
		super(KubeJS.server);
		world = KubeJS.server.world(w.provider.getDimension());
	}
}