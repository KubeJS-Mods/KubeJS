package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.util.Cast;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.resources.ResourceKey;

@RemapPrefixForJS("kjs$")
public interface WithRegistryKeyKJS<T> {
	default RegistryInfo<T> kjs$getKubeRegistry() {
		throw new NoMixinException();
	}

	default ResourceKey<T> kjs$getRegistryKey() {
		return kjs$getKubeRegistry().getKeyOf(Cast.to(this));
	}
}
