package dev.latvian.kubejs.net;

import dev.latvian.kubejs.KubeJS;
import me.shedaniel.architectury.networking.NetworkManager.PacketContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * @author LatvianModder
 */
public class MessageSendDataFromServer {
	private final String channel;
	private final CompoundTag data;

	public MessageSendDataFromServer(String c, @Nullable CompoundTag d) {
		channel = c;
		data = d;
	}

	MessageSendDataFromServer(FriendlyByteBuf buf) {
		channel = buf.readUtf(120);
		data = buf.readNbt();
	}

	void write(FriendlyByteBuf buf) {
		buf.writeUtf(channel, 120);
		buf.writeNbt(data);
	}

	void handle(Supplier<PacketContext> context) {
		if (!channel.isEmpty()) {
			context.get().queue(() -> KubeJS.instance.proxy.handleDataToClientPacket(channel, data));
		}
	}
}