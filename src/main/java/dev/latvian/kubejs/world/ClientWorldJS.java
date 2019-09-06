package dev.latvian.kubejs.world;

import dev.latvian.kubejs.documentation.DocClass;
import dev.latvian.kubejs.player.AttachPlayerDataEvent;
import dev.latvian.kubejs.player.ClientPlayerDataJS;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author LatvianModder
 */
@DocClass("Client side dimension")
public class ClientWorldJS extends WorldJS
{
	private static ClientWorldJS inst;

	public static ClientWorldJS get()
	{
		if (inst == null || inst.world != Minecraft.getMinecraft().world || inst.clientPlayerData.player.player != Minecraft.getMinecraft().player)
		{
			inst = new ClientWorldJS();
			MinecraftForge.EVENT_BUS.post(new AttachWorldDataEvent(inst));
			MinecraftForge.EVENT_BUS.post(new AttachPlayerDataEvent(inst.clientPlayerData));
		}

		return inst;
	}

	public final Minecraft minecraft;
	public final ClientPlayerDataJS clientPlayerData;
	private final Map<UUID, ClientPlayerDataJS> fakePlayers;

	public ClientWorldJS()
	{
		super(Minecraft.getMinecraft().world);
		minecraft = Minecraft.getMinecraft();
		clientPlayerData = new ClientPlayerDataJS(this, minecraft.player.getUniqueID(), minecraft.player.getName());
		fakePlayers = new HashMap<>();
	}

	@Override
	public ClientPlayerDataJS getPlayerData(EntityPlayer player)
	{
		if (player.getUniqueID().equals(clientPlayerData.id))
		{
			return clientPlayerData;
		}

		throw new IllegalStateException("Can't access other client players!");
	}

	@Override
	public String toString()
	{
		return "ClientWorld" + world.provider.getDimension();
	}
}