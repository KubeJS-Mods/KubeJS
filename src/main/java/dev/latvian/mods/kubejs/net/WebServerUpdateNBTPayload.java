package dev.latvian.mods.kubejs.net;

import com.mojang.serialization.JsonOps;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.web.local.KubeJSWeb;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CollectionTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Optional;

public record WebServerUpdateNBTPayload(String event, Optional<Tag> payload) implements CustomPacketPayload {
	public static final StreamCodec<ByteBuf, WebServerUpdateNBTPayload> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.STRING_UTF8, WebServerUpdateNBTPayload::event,
		ByteBufCodecs.optional(ByteBufCodecs.TAG), WebServerUpdateNBTPayload::payload,
		WebServerUpdateNBTPayload::new
	);

	@Override
	public Type<?> type() {
		return KubeJSNet.WEB_SERVER_NBT_UPDATE;
	}

	public void handle(IPayloadContext ctx) {
		if (KubeJSWeb.UPDATES.sessions().isEmpty() && event.equals("highlight/items")) {
			var ops = ctx.player().level().registryAccess().createSerializationContext(NbtOps.INSTANCE);

			for (var e : (CollectionTag<?>) payload.get()) {
				var stack = ItemStack.CODEC.decode(NbtOps.INSTANCE, e).result().get().getFirst();
				KubeJS.LOGGER.info("[Highlighted Item] " + stack.kjs$toItemString0(ops));
			}
		} else {
			KubeJSWeb.broadcastUpdate("server/" + event, () -> payload.map(tag -> NbtOps.INSTANCE.convertTo(JsonOps.INSTANCE, tag)).orElse(null));
		}
	}
}