package dev.latvian.kubejs.world;

import dev.latvian.kubejs.documentation.DocClass;
import dev.latvian.kubejs.documentation.DocField;
import dev.latvian.kubejs.player.AttachPlayerDataEvent;
import dev.latvian.kubejs.player.ServerPlayerDataJS;
import dev.latvian.kubejs.player.ServerPlayerJS;
import dev.latvian.kubejs.server.ServerJS;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author LatvianModder
 */
@DocClass("Server side dimension")
public class ServerWorldJS extends WorldJS
{
	@DocField
	public final ServerJS server;

	private final Map<UUID, ServerPlayerDataJS> fakePlayers;

	public ServerWorldJS(ServerJS s, WorldServer w)
	{
		super(w);
		server = s;
		fakePlayers = new HashMap<>();
	}

	@Override
	@Nullable
	public ServerPlayerDataJS getPlayerData(UUID id)
	{
		return server.playerMap.get(id);
	}

	@Nullable
	@Override
	public ServerPlayerJS createFakePlayer(EntityPlayer player)
	{
		ServerPlayerDataJS p = fakePlayers.get(player.getUniqueID());

		if (p == null)
		{
			p = new ServerPlayerDataJS(server, player.getUniqueID(), player.getName());
			fakePlayers.put(player.getUniqueID(), p);
			MinecraftForge.EVENT_BUS.post(new AttachPlayerDataEvent(p));
		}

		return new ServerPlayerJS(p, this, (EntityPlayerMP) player);
	}

	@Override
	public String toString()
	{
		return "ServerWorld" + world.provider.getDimension();
	}
}