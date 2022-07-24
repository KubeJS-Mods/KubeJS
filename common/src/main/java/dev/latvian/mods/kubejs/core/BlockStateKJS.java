package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.world.level.material.Material;

/**
 * @author LatvianModder
 */
@RemapPrefixForJS("kjs$")
public interface BlockStateKJS {
	default void kjs$setMaterial(Material v) {
		throw new NoMixinException();
	}

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
