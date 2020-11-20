package dev.latvian.kubejs.net;

import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.util.MapJS;
import me.shedaniel.architectury.networking.NetworkManager.PacketContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * @author LatvianModder
 */
public class MessageSendDataFromClient
{
	private final String channel;
	private final CompoundTag data;

	public MessageSendDataFromClient(String c, @Nullable CompoundTag d)
	{
		channel = c;
		data = d;
	}

	MessageSendDataFromClient(FriendlyByteBuf buf)
	{
		channel = buf.readUtf(120);
		data = buf.readNbt();
	}

	void write(FriendlyByteBuf buf)
	{
		buf.writeUtf(channel, 120);
		buf.writeNbt(data);
	}

	void handle(Supplier<PacketContext> context)
	{
		if (!channel.isEmpty())
		{
			final Player player = context.get().getPlayer();

			if (player != null)
			{
				context.get().queue(() -> new NetworkEventJS(player, channel, MapJS.of(data)).post(KubeJSEvents.PLAYER_DATA_FROM_CLIENT, channel));
			}
		}
	}
}