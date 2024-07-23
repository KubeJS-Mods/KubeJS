package dev.latvian.mods.kubejs.net;

import dev.latvian.mods.kubejs.KubeJS;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncServerDataPayload(KubeServerData data) implements CustomPacketPayload {
	public static final StreamCodec<RegistryFriendlyByteBuf, SyncServerDataPayload> STREAM_CODEC = KubeServerData.STREAM_CODEC.map(SyncServerDataPayload::new, SyncServerDataPayload::data);

	@Override
	public Type<?> type() {
		return KubeJSNet.SYNC_SERVER_DATA;
	}

	public void handle(IPayloadContext ctx) {
		ctx.enqueueWork(() -> KubeJS.PROXY.updateServerData(data));
	}
}