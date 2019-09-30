package dev.latvian.kubejs.net;

import dev.latvian.kubejs.text.Text;
import dev.latvian.kubejs.util.JsonUtilsJS;
import dev.latvian.kubejs.util.Overlay;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

/**
 * @author LatvianModder
 */
public class MessageOpenOverlay implements IMessage
{
	public Overlay overlay;

	public MessageOpenOverlay()
	{
	}

	public MessageOpenOverlay(Overlay o)
	{
		overlay = o;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		overlay = new Overlay(ByteBufUtils.readUTF8String(buf));
		overlay.color = buf.readInt();
		overlay.alwaysOnTop = buf.readBoolean();
		int s = buf.readUnsignedByte();

		for (int i = 0; i < s; i++)
		{
			overlay.add(Text.fromJson(JsonUtilsJS.fromString(ByteBufUtils.readUTF8String(buf))));
		}
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeUTF8String(buf, overlay.id);
		buf.writeInt(overlay.color);
		buf.writeBoolean(overlay.alwaysOnTop);
		buf.writeByte(overlay.text.size());

		for (Text t : overlay.text)
		{
			ByteBufUtils.writeUTF8String(buf, JsonUtilsJS.toString(t.getJson()));
		}
	}
}