package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.rhino.util.RemapPrefixForJS;

@RemapPrefixForJS("kjs$")
public interface BlockStateKJS {
	default void kjs$setDestroySpeed(float v) {
		throw new NoMixinException();
	}

	default void kjs$setRequiresTool(boolean v) {
		throw new NoMixinException();
	}

	default void kjs$setLightEmission(int v) {
		throw new NoMixinException();
	}
}
