package dev.latvian.mods.kubejs.net;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.util.NotificationToastData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record NotificationPayload(NotificationToastData data) implements CustomPacketPayload {
	public static final StreamCodec<RegistryFriendlyByteBuf, NotificationPayload> STREAM_CODEC = NotificationToastData.STREAM_CODEC.map(NotificationPayload::new, NotificationPayload::data);

	@Override
	public Type<?> type() {
		return KubeJSNet.NOTIFICATION;
	}

	public void handle(IPayloadContext ctx) {
		ctx.enqueueWork(() -> {
			var p0 = KubeJS.PROXY.getClientPlayer();

			if (p0 != null) {
				p0.kjs$notify(data);
			}
		});
	}
}