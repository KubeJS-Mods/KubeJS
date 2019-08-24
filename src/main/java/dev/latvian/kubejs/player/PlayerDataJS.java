package dev.latvian.kubejs.player;

import dev.latvian.kubejs.server.ServerJS;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author LatvianModder
 */
public class PlayerDataJS
{
	public final ServerJS server;
	public final UUID uuid;
	public final String name;
	public final Map<String, Object> data;

	public PlayerDataJS(ServerJS s, EntityPlayerMP p)
	{
		server = s;
		uuid = p.getGameProfile().getId();
		name = p.getGameProfile().getName();
		data = new HashMap<>();
	}

	public PlayerJS player()
	{
		EntityPlayerMP p = server.server.getPlayerList().getPlayerByUUID(uuid);

		if (p == null)
		{
			throw new NullPointerException("Player entity for " + name + " not found!");
		}

		return new PlayerJS(this, p);
	}
}