package dev.latvian.kubejs.net;

import dev.latvian.kubejs.KubeJS;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @author LatvianModder
 */
public class OpenOverlayHandler implements IMessageHandler<MessageOpenOverlay, IMessage>
{
	@Override
	public IMessage onMessage(MessageOpenOverlay message, MessageContext ctx)
	{
		FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> KubeJS.PROXY.openOverlay(message.overlay));
		return null;
	}
}