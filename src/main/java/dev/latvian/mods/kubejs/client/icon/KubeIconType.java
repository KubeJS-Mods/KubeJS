package dev.latvian.mods.kubejs.client.icon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.latvian.mods.kubejs.KubeJSCodecs;
import dev.latvian.mods.kubejs.KubeJSStreamCodecs;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugins;
import dev.latvian.mods.kubejs.util.Lazy;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public record KubeIconType<T extends KubeIcon>(ResourceLocation id, MapCodec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
	public static final Lazy<Map<ResourceLocation, KubeIconType<?>>> TYPES = Lazy.map(map -> KubeJSPlugins.forEachPlugin(type -> map.put(type.id, type), KubeJSPlugin::registerIconTypes));

	public static final Codec<KubeIconType<?>> CODEC = KubeJSCodecs.KUBEJS_ID.xmap(s -> TYPES.get().get(s), KubeIconType::id);
	public static final StreamCodec<RegistryFriendlyByteBuf, KubeIconType<?>> STREAM_CODEC = KubeJSStreamCodecs.KUBEJS_ID.map(s -> TYPES.get().get(s), KubeIconType::id);

	public KubeIconType(ResourceLocation id, MapCodec<T> codec) {
		this(id, codec, ByteBufCodecs.fromCodecWithRegistries(codec.codec()));
	}
}
