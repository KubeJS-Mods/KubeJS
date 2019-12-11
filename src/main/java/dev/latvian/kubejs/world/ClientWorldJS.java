package dev.latvian.kubejs.world;

import dev.latvian.kubejs.MinecraftClass;
import dev.latvian.kubejs.player.ClientPlayerDataJS;
import dev.latvian.kubejs.player.EntityArrayList;
import dev.latvian.kubejs.script.ScriptType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;

/**
 * @author LatvianModder
 */
public class ClientWorldJS extends WorldJS
{
	public static ClientWorldJS instance;

	private final Minecraft minecraft;
	@MinecraftClass
	public final ClientPlayerEntity minecraftPlayer;
	public final ClientPlayerDataJS clientPlayerData;

	public ClientWorldJS(Minecraft mc, ClientPlayerEntity e)
	{
		super(e.world);
		minecraft = mc;
		minecraftPlayer = e;
		clientPlayerData = new ClientPlayerDataJS(this, minecraftPlayer, true);
	}

	@MinecraftClass
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
		else
		{
			return new ClientPlayerDataJS(this, player, false);
		}
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