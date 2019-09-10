package dev.latvian.kubejs.client;

import dev.latvian.kubejs.KubeJSCommon;
import dev.latvian.kubejs.net.KubeJSNetHandler;
import dev.latvian.kubejs.net.MessageSendData;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class KubeJSClient extends KubeJSCommon
{
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
}