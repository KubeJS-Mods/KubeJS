package dev.latvian.kubejs.world;

import dev.latvian.kubejs.documentation.DocClass;
import dev.latvian.kubejs.documentation.DocField;
import dev.latvian.kubejs.player.AttachPlayerDataEvent;
import dev.latvian.kubejs.player.FakeServerPlayerDataJS;
import dev.latvian.kubejs.player.ServerPlayerDataJS;
import dev.latvian.kubejs.server.ServerJS;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;

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
	public ServerPlayerDataJS getPlayerData(EntityPlayer player)
	{
		ServerPlayerDataJS data = server.playerMap.get(player.getUniqueID());

		if (data != null)
		{
			return data;
		}

		FakeServerPlayerDataJS fakeData = server.fakePlayerMap.get(player.getUniqueID());

		if (fakeData == null)
		{
			fakeData = new FakeServerPlayerDataJS(server, (EntityPlayerMP) player);
			MinecraftForge.EVENT_BUS.post(new AttachPlayerDataEvent(fakeData));
		}

		fakeData.player = (EntityPlayerMP) player;
		return fakeData;
	}

	@Override
	public String toString()
	{
		return "ServerWorld" + world.provider.getDimension();
	}
}