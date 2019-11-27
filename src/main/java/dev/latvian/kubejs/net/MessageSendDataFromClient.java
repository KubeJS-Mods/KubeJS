package dev.latvian.kubejs.net;

import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.util.nbt.NBTBaseJS;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * @author LatvianModder
 */
public class MessageSendDataFromClient
{
	private final String channel;
	private final CompoundNBT data;

	public MessageSendDataFromClient(String c, @Nullable CompoundNBT d)
	{
		channel = c;
		data = d;
	}

	MessageSendDataFromClient(PacketBuffer buf)
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
			final PlayerEntity player = context.get().getSender();

			if (player != null)
			{
				context.get().enqueueWork(() -> new NetworkEventJS(player, channel, NBTBaseJS.of(data).asCompound()).post(KubeJSEvents.PLAYER_DATA_FROM_CLIENT, channel));
			}
		}
	}
}