package dev.latvian.mods.kubejs.net;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.Collection;

public record SyncStagesPayload(Collection<String> stages) implements CustomPacketPayload {
	public static final StreamCodec<ByteBuf, SyncStagesPayload> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.collection(ArrayList::new, ByteBufCodecs.STRING_UTF8),
		SyncStagesPayload::stages,
		SyncStagesPayload::new
	);

	@Override
	public Type<?> type() {
		return KubeJSNet.SYNC_STAGES;
	}

	public void handle(IPayloadContext ctx) {
		ctx.enqueueWork(() -> ctx.player().kjs$getStages().replace(stages));
	}
}