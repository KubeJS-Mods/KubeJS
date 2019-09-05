package dev.latvian.kubejs.client;

import dev.latvian.kubejs.player.ClientPlayerJS;
import dev.latvian.kubejs.world.ClientWorldJS;

/**
 * @author LatvianModder
 */
public class ClientRuntime
{
	public ClientWorldJS getWorld()
	{
		return ClientWorldJS.get();
	}

	public ClientPlayerJS getPlayer()
	{
		return getWorld().clientPlayerData.player;
	}
}