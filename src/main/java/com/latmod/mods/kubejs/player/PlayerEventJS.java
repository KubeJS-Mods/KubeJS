package com.latmod.mods.kubejs.player;

import com.latmod.mods.kubejs.KubeJS;
import com.latmod.mods.kubejs.world.WorldEventJS;
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
		super(KubeJS.server.world(p.world.provider.getDimension()));
		player = KubeJS.server.player(p.getUniqueID());
	}
}