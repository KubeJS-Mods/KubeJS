package dev.latvian.kubejs.net;

import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.event.EventsJS;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @author LatvianModder
 */
public class DataToServerHandler implements IMessageHandler<MessageSendData, IMessage>
{
	@Override
	public IMessage onMessage(MessageSendData message, MessageContext ctx)
	{
		EntityPlayer player = ctx.getServerHandler().player;

		if (player != null)
		{
			NetworkEventJS event = message.getNetworkEvent(player);

			if (!event.getChannel().isEmpty())
			{
				FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> EventsJS.postDouble(KubeJSEvents.PLAYER_DATA_FROM_CLIENT, event.getChannel(), event));
			}
		}

		return null;
	}
}