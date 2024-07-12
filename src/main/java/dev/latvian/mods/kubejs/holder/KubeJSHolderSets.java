package dev.latvian.mods.kubejs.holder;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.latvian.mods.kubejs.KubeJS;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.holdersets.HolderSetType;
import net.neoforged.neoforge.registries.holdersets.ICustomHolderSet;

public interface KubeJSHolderSets {
	DeferredRegister<HolderSetType> REGISTRY = DeferredRegister.create(NeoForgeRegistries.Keys.HOLDER_SET_TYPES, KubeJS.MOD_ID);

	Holder<HolderSetType> REGEX = REGISTRY.register("regex", () -> new HolderSetType() {
		@Override
		public <T> MapCodec<? extends ICustomHolderSet<T>> makeCodec(ResourceKey<? extends Registry<T>> registryKey, Codec<Holder<T>> holderCodec, boolean forceList) {
			return RegExHolderSet.codec(registryKey);
		}

		@Override
		public <T> StreamCodec<RegistryFriendlyByteBuf, ? extends ICustomHolderSet<T>> makeStreamCodec(ResourceKey<? extends Registry<T>> registryKey) {
			return RegExHolderSet.streamCodec(registryKey);
		}
	});

	Holder<HolderSetType> NAMESPACE = REGISTRY.register("namespace", () -> new HolderSetType() {
		@Override
		public <T> MapCodec<? extends ICustomHolderSet<T>> makeCodec(ResourceKey<? extends Registry<T>> registryKey, Codec<Holder<T>> holderCodec, boolean forceList) {
			return NamespaceHolderSet.codec(registryKey);
		}

		@Override
		public <T> StreamCodec<RegistryFriendlyByteBuf, ? extends ICustomHolderSet<T>> makeStreamCodec(ResourceKey<? extends Registry<T>> registryKey) {
			return NamespaceHolderSet.streamCodec(registryKey);
		}
	});
}
