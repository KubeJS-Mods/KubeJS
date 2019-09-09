package dev.latvian.kubejs.net;

import dev.latvian.kubejs.KubeJS;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author LatvianModder
 */
public class KubeJSNetHandler
{
	public static SimpleNetworkWrapper net;

	public static void init()
	{
		net = new SimpleNetworkWrapper(KubeJS.MOD_ID);
		net.registerMessage(new DataToClientHandler(), MessageSendData.class, 1, Side.CLIENT);
		net.registerMessage(new DataToServerHandler(), MessageSendData.class, 2, Side.SERVER);
	}
}