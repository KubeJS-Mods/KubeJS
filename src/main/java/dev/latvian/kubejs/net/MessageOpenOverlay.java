package dev.latvian.kubejs.net;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.text.Text;
import dev.latvian.kubejs.util.Overlay;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * @author LatvianModder
 */
public class MessageOpenOverlay
{
	private final Overlay overlay;

	public MessageOpenOverlay(Overlay o)
	{
		overlay = o;
	}

	MessageOpenOverlay(PacketBuffer buf)
	{
		overlay = new Overlay(buf.readString(5000));
		overlay.color = buf.readInt();
		overlay.alwaysOnTop = buf.readBoolean();
		int s = buf.readUnsignedByte();

		for (int i = 0; i < s; i++)
		{
			overlay.add(Text.of(buf.readTextComponent()));
		}
	}

	void write(PacketBuffer buf)
	{
		buf.writeString(overlay.id, 5000);
		buf.writeInt(overlay.color);
		buf.writeBoolean(overlay.alwaysOnTop);
		buf.writeByte(overlay.text.size());

		for (Text t : overlay.text)
		{
			buf.writeTextComponent(t.component());
		}
	}

	void handle(Supplier<NetworkEvent.Context> context)
	{
		context.get().enqueueWork(() -> KubeJS.instance.proxy.openOverlay(overlay));
	}
}