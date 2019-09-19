package dev.latvian.kubejs.net;

import dev.latvian.kubejs.KubeJS;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @author LatvianModder
 */
public class CloseOverlayHandler implements IMessageHandler<MessageCloseOverlay, IMessage>
{
	@Override
	public IMessage onMessage(MessageCloseOverlay message, MessageContext ctx)
	{
		FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> KubeJS.PROXY.closeOverlay(message.overlay));
		return null;
	}
}