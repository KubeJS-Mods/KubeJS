package dev.latvian.kubejs.net;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.text.Text;
import dev.latvian.kubejs.util.Overlay;
import me.shedaniel.architectury.networking.NetworkManager.PacketContext;
import me.shedaniel.architectury.networking.simple.BaseS2CMessage;
import me.shedaniel.architectury.networking.simple.MessageType;
import net.minecraft.network.FriendlyByteBuf;

/**
 * @author LatvianModder
 */
public class MessageOpenOverlay extends BaseS2CMessage {
	private final Overlay overlay;

	public MessageOpenOverlay(Overlay o) {
		overlay = o;
	}

	MessageOpenOverlay(FriendlyByteBuf buffer) {
		overlay = new Overlay(buffer.readUtf(5000));
		overlay.color = buffer.readInt();
		overlay.alwaysOnTop = buffer.readBoolean();
		int s = buffer.readUnsignedByte();

		for (int i = 0; i < s; i++) {
			overlay.add(Text.read(buffer));
		}
	}

	@Override
	public MessageType getType() {
		return KubeJSNet.OPEN_OVERLAY;
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeUtf(overlay.id, 5000);
		buffer.writeInt(overlay.color);
		buffer.writeBoolean(overlay.alwaysOnTop);
		buffer.writeByte(overlay.text.size());

		for (Text t : overlay.text) {
			t.write(buffer);
		}
	}

	@Override
	public void handle(PacketContext context) {
		KubeJS.PROXY.openOverlay(overlay);
	}
}