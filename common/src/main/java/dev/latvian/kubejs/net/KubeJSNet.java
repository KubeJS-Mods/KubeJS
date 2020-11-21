package dev.latvian.kubejs.net;

import dev.latvian.kubejs.KubeJS;
import me.shedaniel.architectury.networking.NetworkChannel;
import net.minecraft.resources.ResourceLocation;

import static me.shedaniel.architectury.networking.NetworkManager.*;

/**
 * @author LatvianModder
 */
public class KubeJSNet
{
	public static final NetworkChannel MAIN = NetworkChannel.create(new ResourceLocation(KubeJS.MOD_ID, "main"));

	public static void init()
	{
		MAIN.register(clientToServer(), 1, MessageSendDataFromClient.class, MessageSendDataFromClient::write, MessageSendDataFromClient::new, MessageSendDataFromClient::handle);
		MAIN.register(serverToClient(), 2, MessageSendDataFromServer.class, MessageSendDataFromServer::write, MessageSendDataFromServer::new, MessageSendDataFromServer::handle);
		MAIN.register(serverToClient(), 3, MessageOpenOverlay.class, MessageOpenOverlay::write, MessageOpenOverlay::new, MessageOpenOverlay::handle);
		MAIN.register(serverToClient(), 4, MessageCloseOverlay.class, MessageCloseOverlay::write, MessageCloseOverlay::new, MessageCloseOverlay::handle);
	}
}