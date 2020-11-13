package dev.latvian.kubejs.net;

import dev.latvian.kubejs.KubeJS;
import me.shedaniel.architectury.networking.NetworkManager.PacketContext;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Supplier;

/**
 * @author LatvianModder
 */
public class MessageCloseOverlay
{
	private final String overlay;

	public MessageCloseOverlay(String o)
	{
		overlay = o;
	}

	MessageCloseOverlay(FriendlyByteBuf buf)
	{
		overlay = buf.readUtf(5000);
	}

	void write(FriendlyByteBuf buf)
	{
		buf.writeUtf(overlay, 5000);
	}

	void handle(Supplier<PacketContext> context)
	{
		context.get().queue(() -> KubeJS.instance.proxy.closeOverlay(overlay));
	}
}