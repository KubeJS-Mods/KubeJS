package dev.latvian.kubejs.world;

import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.client.ClientLoggedInEventJS;
import dev.latvian.kubejs.player.AttachPlayerDataEvent;
import dev.latvian.kubejs.player.ClientPlayerDataJS;
import dev.latvian.kubejs.player.EntityArrayList;
import dev.latvian.kubejs.script.ScriptType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.common.MinecraftForge;

/**
 * @author LatvianModder
 */
public class ClientWorldJS extends WorldJS
{
	private static ClientWorldJS inst;

	public static ClientWorldJS get()
	{
		if (inst == null || inst.minecraftWorld != Minecraft.getInstance().world)
		{
			inst = new ClientWorldJS();
			MinecraftForge.EVENT_BUS.post(new AttachWorldDataEvent(inst));
			MinecraftForge.EVENT_BUS.post(new AttachPlayerDataEvent(inst.clientPlayerData));
			new ClientLoggedInEventJS(inst.clientPlayerData.getPlayer()).post(KubeJSEvents.CLIENT_LOGGED_IN);
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
		super(Minecraft.getInstance().world);
		minecraft = Minecraft.getInstance();
		clientPlayerData = new ClientPlayerDataJS(this);
	}

	public Minecraft getMinecraft()
	{
		return minecraft;
	}

	@Override
	public ScriptType getSide()
	{
		return ScriptType.CLIENT;
	}

	@Override
	public ClientPlayerDataJS getPlayerData(PlayerEntity player)
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
		return "ClientWorld:" + minecraftWorld.getDimension().getType().getRegistryName();
	}

	@Override
	public EntityArrayList getEntities()
	{
		return new EntityArrayList(this, ((ClientWorld) minecraftWorld).getAllEntities());
	}
}