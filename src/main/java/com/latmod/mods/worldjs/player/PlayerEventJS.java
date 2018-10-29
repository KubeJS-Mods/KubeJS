package com.latmod.mods.worldjs.player;

import com.latmod.mods.worldjs.WorldJSMod;
import com.latmod.mods.worldjs.world.WorldEventJS;
import net.minecraft.entity.Entity;

/**
 * @author LatvianModder
 */
public class PlayerEventJS extends WorldEventJS
{
	public final PlayerJS player;

	public PlayerEventJS(PlayerJS p)
	{
		super(p.world);
		player = p;
	}

	public PlayerEventJS(Entity p)
	{
		super(WorldJSMod.server.getWorld(p.world.provider.getDimension()));
		player = WorldJSMod.server.getPlayer(p.getUniqueID());
	}
}