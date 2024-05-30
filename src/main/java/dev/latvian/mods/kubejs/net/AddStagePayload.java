package dev.latvian.mods.kubejs.net;

import dev.latvian.mods.kubejs.KubeJS;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public record AddStagePayload(UUID player, String stage) implements CustomPacketPayload {
	public static final StreamCodec<ByteBuf, AddStagePayload> STREAM_CODEC = StreamCodec.composite(
		UUIDUtil.STREAM_CODEC, AddStagePayload::player,
		ByteBufCodecs.STRING_UTF8, AddStagePayload::stage,
		AddStagePayload::new
	);

	@Override
	public Type<?> type() {
		return KubeJSNet.ADD_STAGE;
	}

	public void handle(IPayloadContext ctx) {
		var p0 = KubeJS.PROXY.getClientPlayer();

		if (p0 == null) {
			return;
		}

		ctx.enqueueWork(() -> {
			var p = player.equals(p0.getUUID()) ? p0 : p0.level().getPlayerByUUID(player);

			if (p != null) {
				p.kjs$getStages().add(stage);
			}
		});
	}
}