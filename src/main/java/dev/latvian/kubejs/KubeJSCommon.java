package dev.latvian.kubejs;

import dev.latvian.kubejs.net.KubeJSNetHandler;
import dev.latvian.kubejs.net.MessageSendData;
import dev.latvian.kubejs.util.Overlay;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class KubeJSCommon
{
	public void sendData(EntityPlayer playerEntity, String channel, @Nullable NBTTagCompound data)
	{
		if (!playerEntity.world.isRemote && !channel.isEmpty())
		{
			KubeJSNetHandler.net.sendTo(new MessageSendData(channel, data), (EntityPlayerMP) playerEntity);
		}
	}

	@Nullable
	public EntityPlayer getClientPlayer()
	{
		return null;
	}

	public void openOverlay(Overlay o)
	{
	}

	public void closeOverlay(String id)
	{
	}
}