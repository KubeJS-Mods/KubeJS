package dev.latvian.kubejs.net;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.text.Text;
import dev.latvian.kubejs.util.Overlay;
import me.shedaniel.architectury.networking.NetworkManager.PacketContext;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Supplier;

/**
 * @author LatvianModder
 */
public class MessageOpenOverlay {
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

	void write(FriendlyByteBuf buffer) {
		buffer.writeUtf(overlay.id, 5000);
		buffer.writeInt(overlay.color);
		buffer.writeBoolean(overlay.alwaysOnTop);
		buffer.writeByte(overlay.text.size());

		for (Text t : overlay.text) {
			t.write(buffer);
		}
	}

	void handle(Supplier<PacketContext> context) {
		context.get().queue(() -> KubeJS.PROXY.openOverlay(overlay));
	}
}