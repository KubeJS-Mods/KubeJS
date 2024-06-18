package dev.latvian.mods.kubejs.net;

import dev.latvian.mods.kubejs.KubeJS;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record RequestBlockKubedexPayload(BlockPos pos) implements CustomPacketPayload {
	public static final StreamCodec<ByteBuf, RequestBlockKubedexPayload> STREAM_CODEC = StreamCodec.composite(
		BlockPos.STREAM_CODEC, RequestBlockKubedexPayload::pos,
		RequestBlockKubedexPayload::new
	);

	@Override
	public Type<?> type() {
		return KubeJSNet.REQUEST_BLOCK_KUBEDEX;
	}

	public void handle(IPayloadContext ctx) {
		if (ctx.player() instanceof ServerPlayer serverPlayer && serverPlayer.hasPermissions(2)) {
			ctx.enqueueWork(() -> {
				var registries = serverPlayer.server.registryAccess();
				var blockState = serverPlayer.level().getBlockState(pos);

				if (!blockState.isAir()) {
					KubeJS.LOGGER.info("[Kubedex][" + serverPlayer.getScoreboardName() + "] Block State " + blockState + " @ " + pos);
				}

				var blockEntity = serverPlayer.level().getBlockEntity(pos);

				if (blockEntity != null) {
					KubeJS.LOGGER.info("[Kubedex][" + serverPlayer.getScoreboardName() + "] Block Entity " + blockEntity.saveWithoutMetadata(registries));
				}
			});
		}
	}
}