package dev.latvian.mods.kubejs.net;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Optional;

public record SetActivePostShaderPayload(Optional<ResourceLocation> id) implements CustomPacketPayload {
	public static final StreamCodec<ByteBuf, SetActivePostShaderPayload> STREAM_CODEC = ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC).map(SetActivePostShaderPayload::new, SetActivePostShaderPayload::id);

	@Override
	public Type<?> type() {
		return KubeJSNet.SET_ACTIVE_POST_SHADER;
	}

	public void handle(IPayloadContext ctx) {
		ctx.enqueueWork(() -> ctx.player().kjs$setActivePostShader(id.orElse(null)));
	}
}