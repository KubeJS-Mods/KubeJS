package dev.latvian.kubejs.net;

import dev.latvian.kubejs.KubeJS;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * @author LatvianModder
 */
public class MessageSendDataFromServer
{
	private final String channel;
	private final CompoundNBT data;

	public MessageSendDataFromServer(String c, @Nullable CompoundNBT d)
	{
		channel = c;
		data = d;
	}

	MessageSendDataFromServer(PacketBuffer buf)
	{
		channel = buf.readString(120);
		data = buf.readCompoundTag();
	}

	void write(PacketBuffer buf)
	{
		buf.writeString(channel, 120);
		buf.writeCompoundTag(data);
	}

	void handle(Supplier<NetworkEvent.Context> context)
	{
		if (!channel.isEmpty())
		{
			context.get().enqueueWork(() -> KubeJS.instance.proxy.handleDataToClientPacket(channel, data));
			context.get().setPacketHandled(true);
		}
	}
}