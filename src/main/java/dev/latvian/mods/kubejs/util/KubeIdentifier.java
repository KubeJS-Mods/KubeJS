package dev.latvian.mods.kubejs.util;

import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.codec.KubeJSCodecs;
import net.minecraft.resources.Identifier;

import java.util.function.UnaryOperator;

/// Exists to indicate that a Identifier would use kubejs: namespace by default when written as plain string. Should only be used as an argument in registry methods
public record KubeIdentifier(Identifier wrapped) {
	public static final Codec<KubeIdentifier> CODEC = KubeJSCodecs.KUBEJS_ID.xmap(KubeIdentifier::new, KubeIdentifier::wrapped);

	public static KubeIdentifier wrap(Object from) {
		return new KubeIdentifier(ID.kjs(from));
	}

	@Override
	public String toString() {
		return wrapped.toString();
	}

	public KubeIdentifier withPath(String path) {
		return new KubeIdentifier(wrapped.withPath(path));
	}

	public KubeIdentifier withPath(UnaryOperator<String> path) {
		return new KubeIdentifier(wrapped.withPath(path));
	}
}
