package dev.latvian.kubejs.world;

import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.client.ClientLoggedInEventJS;
import dev.latvian.kubejs.event.EventsJS;
import dev.latvian.kubejs.player.AttachPlayerDataEvent;
import dev.latvian.kubejs.player.ClientPlayerDataJS;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;

/**
 * @author LatvianModder
 */
public class ClientWorldJS extends WorldJS
{
	private static ClientWorldJS inst;

	public static ClientWorldJS get()
	{
		if (inst == null || inst.minecraftWorld != Minecraft.getMinecraft().world)
		{
			inst = new ClientWorldJS();
			MinecraftForge.EVENT_BUS.post(new AttachWorldDataEvent(inst));
			MinecraftForge.EVENT_BUS.post(new AttachPlayerDataEvent(inst.clientPlayerData));
			EventsJS.post(KubeJSEvents.CLIENT_LOGGED_IN, new ClientLoggedInEventJS(inst.clientPlayerData.getPlayer()));
		}

		return inst;
	}

	public static void invalidate()
	{
		inst = null;
	}

	private final Minecraft minecraft;
	public final ClientPlayerDataJS clientPlayerData;

	public ClientWorldJS()
	{
		super(Minecraft.getMinecraft().world);
		minecraft = Minecraft.getMinecraft();
		clientPlayerData = new ClientPlayerDataJS(this);
	}

	public Minecraft getMinecraft()
	{
		return minecraft;
	}

	@Override
	public ClientPlayerDataJS getPlayerData(EntityPlayer player)
	{
		if (player.getUniqueID().equals(clientPlayerData.getId()))
		{
			return clientPlayerData;
		}

		throw new IllegalStateException("Can't access other client players!");
	}

	@Override
	public String toString()
	{
		return "ClientWorld" + minecraftWorld.provider.getDimension();
	}
}