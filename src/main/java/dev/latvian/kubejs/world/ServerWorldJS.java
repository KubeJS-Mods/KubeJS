package dev.latvian.kubejs.world;

import dev.latvian.kubejs.documentation.DocClass;
import dev.latvian.kubejs.documentation.DocField;
import dev.latvian.kubejs.player.PlayerDataJS;
import dev.latvian.kubejs.server.ServerJS;
import net.minecraft.world.WorldServer;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * @author LatvianModder
 */
@DocClass("Server side dimension")
public class ServerWorldJS extends WorldJS
{
	@DocField
	public final ServerJS server;

	public ServerWorldJS(ServerJS s, WorldServer w)
	{
		super(w);
		server = s;
	}

	@Override
	@Nullable
	public PlayerDataJS getPlayerData(UUID id)
	{
		return server.playerMap.get(id);
	}
}