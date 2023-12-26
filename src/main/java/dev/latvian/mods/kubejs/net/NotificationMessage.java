package dev.latvian.mods.kubejs.net;

import dev.architectury.networking.NetworkManager.PacketContext;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.util.NotificationBuilder;
import net.minecraft.network.FriendlyByteBuf;

public class NotificationMessage extends BaseS2CMessage {
	private final NotificationBuilder notification;

	public NotificationMessage(NotificationBuilder notification) {
		this.notification = notification;
	}

	NotificationMessage(FriendlyByteBuf buf) {
		notification = new NotificationBuilder(buf);
	}

	@Override
	public MessageType getType() {
		return KubeJSNet.NOTIFICATION;
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		notification.write(buf);
	}

	@Override
	public void handle(PacketContext context) {
		var p0 = KubeJS.PROXY.getClientPlayer();

		if (p0 == null) {
			return;
		}

		p0.kjs$notify(notification);
	}
}