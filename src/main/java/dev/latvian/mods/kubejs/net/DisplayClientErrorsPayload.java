package dev.latvian.mods.kubejs.net;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.script.ScriptType;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record DisplayClientErrorsPayload() implements CustomPacketPayload {
	public static final StreamCodec<ByteBuf, DisplayClientErrorsPayload> STREAM_CODEC = StreamCodec.unit(new DisplayClientErrorsPayload());

	@Override
	public Type<?> type() {
		return KubeJSNet.DISPLAY_CLIENT_ERRORS;
	}

	public void handle(IPayloadContext ctx) {
		ctx.enqueueWork(() -> KubeJS.PROXY.openErrors(ScriptType.CLIENT));
	}
}