package dev.latvian.kubejs.client;

import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.player.ClientPlayerJS;
import dev.latvian.kubejs.player.PlayerEventJS;

/**
 * @author LatvianModder
 */
public class ClientLoggedInEventJS extends PlayerEventJS
{
	private final ClientPlayerJS player;

	public ClientLoggedInEventJS(ClientPlayerJS p)
	{
		player = p;
	}

	@Override
	public EntityJS getEntity()
	{
		return player;
	}
}