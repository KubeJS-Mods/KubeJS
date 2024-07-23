package dev.latvian.mods.kubejs.util;

import net.minecraft.resources.ResourceLocation;

/**
 * Exists to indicate that a ResourceLocation would use kubejs: namespace by default when written as plain string. Should only be used as an argument in registry methods
 */
public record KubeResourceLocation(ResourceLocation wrapped) {
	public static KubeResourceLocation wrap(Object from) {
		return new KubeResourceLocation(ID.kjs(from));
	}

	@Override
	public String toString() {
		return wrapped.toString();
	}
}
