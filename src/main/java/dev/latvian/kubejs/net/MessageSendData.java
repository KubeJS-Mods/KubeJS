package dev.latvian.kubejs.net;

import dev.latvian.kubejs.util.nbt.NBTBaseJS;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
public class MessageSendData implements IMessage
{
	private String channel;
	private NBTTagCompound data;

	public MessageSendData()
	{
	}

	public MessageSendData(String c, @Nullable Object d)
	{
		channel = c;
		data = NBTBaseJS.of(d).asCompound().createNBT();
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		channel = ByteBufUtils.readUTF8String(buf);
		data = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeUTF8String(buf, channel);
		ByteBufUtils.writeTag(buf, data);
	}

	public NetworkEventJS getNetworkEvent(EntityPlayer player)
	{
		return new NetworkEventJS(player, channel, NBTBaseJS.of(data).asCompound());
	}
}