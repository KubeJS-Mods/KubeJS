package dev.latvian.kubejs.net;

import dev.latvian.kubejs.KubeJS;
import net.minecraft.network.PacketBuffer;
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

	MessageCloseOverlay(PacketBuffer buf)
	{
		overlay = buf.readString(5000);
	}

	void write(PacketBuffer buf)
	{
		buf.writeString(overlay, 5000);
	}

	void handle(Supplier<NetworkEvent.Context> context)
	{
		context.get().enqueueWork(() -> KubeJS.instance.proxy.closeOverlay(overlay));
	}
}