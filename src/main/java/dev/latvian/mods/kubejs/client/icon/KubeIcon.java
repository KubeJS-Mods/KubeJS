package dev.latvian.mods.kubejs.client.icon;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public interface KubeIcon {
	Codec<KubeIcon> CODEC = KubeIconType.CODEC.dispatch("type", KubeIcon::getType, KubeIconType::codec);
	StreamCodec<RegistryFriendlyByteBuf, KubeIcon> STREAM_CODEC = KubeIconType.STREAM_CODEC.dispatch(KubeIcon::getType, KubeIconType::streamCodec);
	StreamCodec<RegistryFriendlyByteBuf, Optional<KubeIcon>> OPTIONAL_STREAM_CODEC = ByteBufCodecs.optional(STREAM_CODEC);

	KubeIconType<?> getType();
}
