package dev.latvian.mods.kubejs.net;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSCodecs;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public record SyncStagesPayload(UUID player, Collection<String> stages) implements CustomPacketPayload {
	public static final StreamCodec<ByteBuf, SyncStagesPayload> STREAM_CODEC = StreamCodec.composite(
		KubeJSCodecs.UUID_STREAM_CODEC,
		SyncStagesPayload::player,
		ByteBufCodecs.collection(ArrayList::new, ByteBufCodecs.STRING_UTF8),
		SyncStagesPayload::stages,
		SyncStagesPayload::new
	);

	@Override
	public Type<?> type() {
		return KubeJSNet.SYNC_STAGES;
	}

	public void handle(IPayloadContext ctx) {
		var p0 = KubeJS.PROXY.getClientPlayer();

		if (p0 == null) {
			return;
		}

		ctx.enqueueWork(() -> {
			var p = player.equals(p0.getUUID()) ? p0 : p0.level().getPlayerByUUID(player);

			if (p != null) {
				p.kjs$getStages().replace(stages);
			}
		});
	}
}