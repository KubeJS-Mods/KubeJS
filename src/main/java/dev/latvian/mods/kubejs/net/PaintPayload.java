package dev.latvian.mods.kubejs.net;

import dev.latvian.mods.kubejs.KubeJS;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PaintPayload(CompoundTag tag) implements CustomPacketPayload {
	public static final StreamCodec<ByteBuf, PaintPayload> STREAM_CODEC = ByteBufCodecs.COMPOUND_TAG.map(PaintPayload::new, PaintPayload::tag);

	@Override
	public Type<?> type() {
		return KubeJSNet.PAINT;
	}

	public void handle(IPayloadContext ctx) {
		ctx.enqueueWork(() -> KubeJS.PROXY.paint(tag));
	}
}