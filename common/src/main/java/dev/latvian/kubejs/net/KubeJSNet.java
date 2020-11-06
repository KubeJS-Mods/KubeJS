package dev.latvian.kubejs.net;

import dev.latvian.kubejs.KubeJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.function.Predicate;

/**
 * @author LatvianModder
 */
public class KubeJSNet
{
	public static SimpleChannel MAIN;
	private static final String MAIN_VERSION = "1";

	public static void init()
	{
		Predicate<String> validator = v -> MAIN_VERSION.equals(v) || NetworkRegistry.ABSENT.equals(v) || NetworkRegistry.ACCEPTVANILLA.equals(v);

		MAIN = NetworkRegistry.ChannelBuilder
				.named(new ResourceLocation(KubeJS.MOD_ID, "main"))
				.clientAcceptedVersions(validator)
				.serverAcceptedVersions(validator)
				.networkProtocolVersion(() -> MAIN_VERSION)
				.simpleChannel();

		MAIN.registerMessage(1, MessageSendDataFromClient.class, MessageSendDataFromClient::write, MessageSendDataFromClient::new, MessageSendDataFromClient::handle);
		MAIN.registerMessage(2, MessageSendDataFromServer.class, MessageSendDataFromServer::write, MessageSendDataFromServer::new, MessageSendDataFromServer::handle);
		MAIN.registerMessage(3, MessageOpenOverlay.class, MessageOpenOverlay::write, MessageOpenOverlay::new, MessageOpenOverlay::handle);
		MAIN.registerMessage(4, MessageCloseOverlay.class, MessageCloseOverlay::write, MessageCloseOverlay::new, MessageCloseOverlay::handle);
	}
}