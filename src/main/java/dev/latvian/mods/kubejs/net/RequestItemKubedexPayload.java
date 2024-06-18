package dev.latvian.mods.kubejs.net;

import dev.latvian.mods.kubejs.KubeJS;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.LinkedHashSet;
import java.util.List;

public record RequestItemKubedexPayload(List<Integer> slots, List<ItemStack> stacks) implements CustomPacketPayload {
	public static final StreamCodec<RegistryFriendlyByteBuf, RequestItemKubedexPayload> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.VAR_INT.apply(ByteBufCodecs.list()), RequestItemKubedexPayload::slots,
		ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()), RequestItemKubedexPayload::stacks,
		RequestItemKubedexPayload::new
	);

	@Override
	public Type<?> type() {
		return KubeJSNet.REQUEST_ITEM_KUBEDEX;
	}

	public void handle(IPayloadContext ctx) {
		if (ctx.player() instanceof ServerPlayer serverPlayer && serverPlayer.hasPermissions(2)) {
			ctx.enqueueWork(() -> {
				var ops = serverPlayer.server.registryAccess().createSerializationContext(NbtOps.INSTANCE);
				var allStacks = new LinkedHashSet<>(stacks);

				for (int s : slots) {
					if (s >= 0 && s < serverPlayer.getInventory().getContainerSize()) {
						var item = serverPlayer.getInventory().getItem(s);

						if (!item.isEmpty()) {
							allStacks.add(item);
						}
					}
				}

				for (var stack : allStacks) {
					KubeJS.LOGGER.info("[Kubedex][" + serverPlayer.getScoreboardName() + "] Item " + stack.kjs$toItemString0(ops));
				}
			});
		}
	}
}