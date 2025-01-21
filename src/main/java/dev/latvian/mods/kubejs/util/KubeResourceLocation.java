package dev.latvian.mods.kubejs.util;

import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.KubeJSCodecs;
import net.minecraft.resources.ResourceLocation;

import java.util.function.UnaryOperator;

/**
 * Exists to indicate that a ResourceLocation would use kubejs: namespace by default when written as plain string. Should only be used as an argument in registry methods
 */
public record KubeResourceLocation(ResourceLocation wrapped) {
	public static final Codec<KubeResourceLocation> CODEC = KubeJSCodecs.KUBEJS_ID.xmap(KubeResourceLocation::new, KubeResourceLocation::wrapped);

	public static KubeResourceLocation wrap(Object from) {
		return new KubeResourceLocation(ID.kjs(from));
	}

	@Override
	public String toString() {
		return wrapped.toString();
	}

	public KubeResourceLocation withPath(String path) {
		return new KubeResourceLocation(wrapped.withPath(path));
	}

	public KubeResourceLocation withPath(UnaryOperator<String> path) {
		return new KubeResourceLocation(wrapped.withPath(path));
	}
}
