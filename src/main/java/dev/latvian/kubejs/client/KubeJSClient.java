package dev.latvian.kubejs.client;

import dev.latvian.kubejs.KubeJSCommon;
import dev.latvian.kubejs.net.KubeJSNetHandler;
import dev.latvian.kubejs.net.MessageSendData;
import dev.latvian.kubejs.util.Overlay;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class KubeJSClient extends KubeJSCommon
{
	public static final Map<String, Overlay> activeOverlays = new LinkedHashMap<>();

	@Override
	public void sendData(EntityPlayer playerEntity, String channel, @Nullable NBTTagCompound data)
	{
		if (playerEntity.world.isRemote && !channel.isEmpty())
		{
			KubeJSNetHandler.net.sendToServer(new MessageSendData(channel, data));
		}
		else
		{
			super.sendData(playerEntity, channel, data);
		}
	}

	@Override
	@Nullable
	public EntityPlayer getClientPlayer()
	{
		return Minecraft.getMinecraft().player;
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