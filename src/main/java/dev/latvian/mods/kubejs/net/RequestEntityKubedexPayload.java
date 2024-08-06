package dev.latvian.mods.kubejs.net;

import dev.latvian.mods.kubejs.kubedex.KubedexPayloadHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record RequestEntityKubedexPayload(int entityId) implements CustomPacketPayload {
	public static final StreamCodec<ByteBuf, RequestEntityKubedexPayload> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.VAR_INT, RequestEntityKubedexPayload::entityId,
		RequestEntityKubedexPayload::new
	);

	@Override
	public Type<?> type() {
		return KubeJSNet.Kubedex.REQUEST_ENTITY;
	}

	public void handle(IPayloadContext ctx) {
		if (ctx.player() instanceof ServerPlayer serverPlayer && serverPlayer.hasPermissions(2)) {
			ctx.enqueueWork(() -> KubedexPayloadHandler.entity(serverPlayer, entityId));
		}
	}
}