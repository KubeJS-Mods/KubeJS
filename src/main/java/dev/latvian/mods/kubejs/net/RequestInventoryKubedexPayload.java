package dev.latvian.mods.kubejs.net;

import net.minecraft.commands.Commands;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public record RequestInventoryKubedexPayload(List<Integer> slots, List<ItemStack> stacks, int flags) implements CustomPacketPayload {
	public static final StreamCodec<RegistryFriendlyByteBuf, RequestInventoryKubedexPayload> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.VAR_INT.apply(ByteBufCodecs.list()), RequestInventoryKubedexPayload::slots,
		ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()), RequestInventoryKubedexPayload::stacks,
		ByteBufCodecs.VAR_INT, RequestInventoryKubedexPayload::flags,
		RequestInventoryKubedexPayload::new
	);

	@Override
	public Type<?> type() {
		return KubeJSNet.Kubedex.REQUEST_INVENTORY;
	}

	public void handle(IPayloadContext ctx) {
		if (ctx.player() instanceof ServerPlayer serverPlayer
			&& Commands.LEVEL_GAMEMASTERS.check(serverPlayer.permissions())) {
			// TODO: empty for now, waiting for a kubedex rework
		}
	}
}