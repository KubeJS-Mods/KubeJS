package dev.latvian.mods.kubejs.net;

import dev.latvian.mods.kubejs.recipe.viewer.server.RecipeViewerData;
import dev.latvian.mods.kubejs.recipe.viewer.server.RecipeViewerDataUpdatedEvent;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Optional;

public record SyncRecipeViewerDataPayload(Optional<RecipeViewerData> data) implements CustomPacketPayload {
	public static final StreamCodec<RegistryFriendlyByteBuf, SyncRecipeViewerDataPayload> STREAM_CODEC = ByteBufCodecs.optional(RecipeViewerData.STREAM_CODEC).map(SyncRecipeViewerDataPayload::new, SyncRecipeViewerDataPayload::data);

	@Override
	public Type<?> type() {
		return KubeJSNet.SYNC_RECIPE_VIEWER;
	}

	public void handle(IPayloadContext ctx) {
		ctx.enqueueWork(() -> NeoForge.EVENT_BUS.post(new RecipeViewerDataUpdatedEvent(data.orElse(null))));
	}
}