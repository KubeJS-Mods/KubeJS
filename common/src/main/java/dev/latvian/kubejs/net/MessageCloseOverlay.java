package dev.latvian.kubejs.net;

import dev.latvian.kubejs.KubeJS;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.network.NetworkEvent;

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

	void handle(Supplier<NetworkEvent.Context> context)
	{
		context.get().enqueueWork(() -> KubeJS.instance.proxy.closeOverlay(overlay));
		context.get().setPacketHandled(true);
	}
}