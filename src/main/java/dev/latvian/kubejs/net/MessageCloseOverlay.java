package dev.latvian.kubejs.net;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

/**
 * @author LatvianModder
 */
public class MessageCloseOverlay implements IMessage
{
	public String overlay;

	public MessageCloseOverlay()
	{
	}

	public MessageCloseOverlay(String o)
	{
		overlay = o;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		overlay = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeUTF8String(buf, overlay);
	}
}