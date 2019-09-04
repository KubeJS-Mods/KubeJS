package dev.latvian.kubejs.player;

import dev.latvian.kubejs.server.ServerJS;
import net.minecraft.entity.player.EntityPlayerMP;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * @author LatvianModder
 */
public class ServerPlayerDataJS extends PlayerDataJS<EntityPlayerMP, ServerPlayerJS>
{
	public final ServerJS server;

	public ServerPlayerDataJS(ServerJS s, UUID id, String n)
	{
		super(id, n);
		server = s;
	}

	@Override
	@Nullable
	public EntityPlayerMP getPlayerEntity()
	{
		return server.server.getPlayerList().getPlayerByUUID(uuid);
	}

	@Override
	public ServerPlayerJS player()
	{
		EntityPlayerMP p = getPlayerEntity();

		if (p == null)
		{
			throw new NullPointerException("Player entity for " + name + " not found!");
		}

		return new ServerPlayerJS(this, server.world(p.world), p);
	}
}