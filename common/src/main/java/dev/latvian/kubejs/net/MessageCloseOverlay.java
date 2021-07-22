package dev.latvian.kubejs.net;

import dev.latvian.kubejs.KubeJS;
import me.shedaniel.architectury.networking.NetworkManager.PacketContext;
import me.shedaniel.architectury.networking.simple.BaseS2CMessage;
import me.shedaniel.architectury.networking.simple.MessageType;
import net.minecraft.network.FriendlyByteBuf;

/**
 * @author LatvianModder
 */
public class MessageCloseOverlay extends BaseS2CMessage {
	private final String overlay;

	public MessageCloseOverlay(String o) {
		overlay = o;
	}

	MessageCloseOverlay(FriendlyByteBuf buf) {
		overlay = buf.readUtf(5000);
	}

	@Override
	public MessageType getType() {
		return KubeJSNet.CLOSE_OVERLAY;
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeUtf(overlay, 5000);
	}

	@Override
	public void handle(PacketContext context) {
		KubeJS.PROXY.closeOverlay(overlay);
	}
}