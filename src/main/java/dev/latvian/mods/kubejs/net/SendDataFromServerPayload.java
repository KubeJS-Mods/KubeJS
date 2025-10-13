package dev.latvian.mods.kubejs.net;

import dev.latvian.mods.kubejs.KubeJS;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public record SendDataFromServerPayload(String channel, CompoundTag data) implements CustomPacketPayload {
	public SendDataFromServerPayload(String channel, @Nullable CompoundTag data) {
		this.channel = channel;
		this.data = Objects.requireNonNullElseGet(data, CompoundTag::new);
	}

	public static final StreamCodec<ByteBuf, SendDataFromServerPayload> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.STRING_UTF8, SendDataFromServerPayload::channel,
		ByteBufCodecs.COMPOUND_TAG, SendDataFromServerPayload::data,
		SendDataFromServerPayload::new
	);

	@Override
	public Type<?> type() {
		return KubeJSNet.SEND_DATA_FROM_SERVER;
	}

	public void handle(IPayloadContext ctx) {
		if (!channel.isEmpty()) {
			ctx.enqueueWork(() -> KubeJS.PROXY.handleDataFromServerPacket(channel, data));
		}
	}
}