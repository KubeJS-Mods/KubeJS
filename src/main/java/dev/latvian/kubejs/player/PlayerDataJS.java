package dev.latvian.kubejs.player;

import dev.latvian.kubejs.server.ServerJS;
import net.minecraft.entity.player.EntityPlayerMP;

import javax.annotation.Nullable;
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

	@Nullable
	public EntityPlayerMP getPlayerEntity()
	{
		return server.server.getPlayerList().getPlayerByUUID(uuid);
	}

	public PlayerJS player()
	{
		EntityPlayerMP p = getPlayerEntity();

		if (p == null)
		{
			throw new NullPointerException("Player entity for " + name + " not found!");
		}

		return new PlayerJS(this, p);
	}
}