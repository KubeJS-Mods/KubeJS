package dev.latvian.mods.kubejs.net;

import dev.latvian.mods.kubejs.bindings.event.NetworkEvents;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SendDataFromClientPayload(String channel, CompoundTag data) implements CustomPacketPayload {
	public static final StreamCodec<FriendlyByteBuf, SendDataFromClientPayload> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.STRING_UTF8,
		SendDataFromClientPayload::channel,
		ByteBufCodecs.COMPOUND_TAG,
		SendDataFromClientPayload::data,
		SendDataFromClientPayload::new
	);

	@Override
	public Type<?> type() {
		return KubeJSNet.SEND_DATA_FROM_CLIENT;
	}

	public void handle(IPayloadContext ctx) {
		if (!channel.isEmpty() && ctx.player() instanceof ServerPlayer serverPlayer && NetworkEvents.DATA_RECEIVED.hasListeners(channel)) {
			ctx.enqueueWork(() -> NetworkEvents.DATA_RECEIVED.post(ScriptType.SERVER, channel, new NetworkEventJS(serverPlayer, channel, data)));
		}
	}
}