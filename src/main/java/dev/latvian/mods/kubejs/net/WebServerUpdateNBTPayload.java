package dev.latvian.mods.kubejs.net;

import com.mojang.serialization.JsonOps;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.web.local.KubeJSWeb;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Optional;

public record WebServerUpdateNBTPayload(String event, String requiredTag, Optional<Tag> payload) implements CustomPacketPayload {
	public static final StreamCodec<ByteBuf, WebServerUpdateNBTPayload> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.STRING_UTF8, WebServerUpdateNBTPayload::event,
		ByteBufCodecs.STRING_UTF8, WebServerUpdateNBTPayload::requiredTag,
		ByteBufCodecs.optional(ByteBufCodecs.TAG), WebServerUpdateNBTPayload::payload,
		WebServerUpdateNBTPayload::new
	);

	@Override
	public Type<?> type() {
		return KubeJSNet.WEB_SERVER_NBT_UPDATE;
	}

	public void handle(IPayloadContext ctx) {
		int count = KubeJSWeb.broadcastUpdate("server/" + event, requiredTag, () -> payload.map(tag -> NbtOps.INSTANCE.convertTo(JsonOps.INSTANCE, tag)).orElse(null));

		if (count == 0 && event.equals("highlight/items")) {
			for (var e : ((CompoundTag) payload.get()).getList("items", Tag.TAG_COMPOUND)) {
				var t = (CompoundTag) e;
				KubeJS.LOGGER.info("[Highlighted Item] {}", t.getString("string"));

				if (t.get("tags") instanceof ListTag l) {
					for (var tag : l) {
						KubeJS.LOGGER.info("[Highlighted Item] - #{}", tag.getAsString());
					}
				}
			}
		}
	}
}