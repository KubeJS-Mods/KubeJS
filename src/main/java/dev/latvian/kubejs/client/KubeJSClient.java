package dev.latvian.kubejs.client;

import dev.latvian.kubejs.KubeJSCommon;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.net.KubeJSNet;
import dev.latvian.kubejs.net.MessageSendDataFromClient;
import dev.latvian.kubejs.net.NetworkEventJS;
import dev.latvian.kubejs.util.Overlay;
import dev.latvian.kubejs.util.nbt.NBTBaseJS;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nullable;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class KubeJSClient extends KubeJSCommon
{
	public static final Map<String, Overlay> activeOverlays = new LinkedHashMap<>();

	@Override
	public void init(File folder)
	{
		new KubeJSClientEventHandler().init();
		Minecraft.getInstance().getResourcePackList().addPackFinder(new KubeJSResourcePackFinder(folder));
	}

	@Override
	public void sendData(PlayerEntity playerEntity, String channel, @Nullable CompoundNBT data)
	{
		if (playerEntity.world.isRemote && !channel.isEmpty())
		{
			KubeJSNet.MAIN.sendToServer(new MessageSendDataFromClient(channel, data));
		}
		else
		{
			super.sendData(playerEntity, channel, data);
		}
	}

	@Override
	public void handleDataToClientPacket(String channel, @Nullable CompoundNBT data)
	{
		new NetworkEventJS(Minecraft.getInstance().player, channel, NBTBaseJS.of(data).asCompound()).post(KubeJSEvents.PLAYER_DATA_FROM_SERVER, channel);
	}

	@Override
	@Nullable
	public PlayerEntity getClientPlayer()
	{
		return Minecraft.getInstance().player;
	}

	@Override
	public void openOverlay(Overlay o)
	{
		activeOverlays.put(o.id, o);
	}

	@Override
	public void closeOverlay(String id)
	{
		activeOverlays.remove(id);
	}
}