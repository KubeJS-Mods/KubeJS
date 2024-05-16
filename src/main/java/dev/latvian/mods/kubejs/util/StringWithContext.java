package dev.latvian.mods.kubejs.util;

import dev.latvian.mods.kubejs.script.KubeJSContext;
import dev.latvian.mods.rhino.Context;

public record StringWithContext(KubeJSContext cx, String string) {
	public static StringWithContext of(Context cx, Object o) {
		if (cx instanceof KubeJSContext kcx) {
			return new StringWithContext(kcx, o.toString());
		} else {
			throw new IllegalArgumentException("Context is not a KubeJSContext");
		}
	}

	@Override
	public int hashCode() {
		return string.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		return o != null && toString().equals(o.toString());
	}

	@Override
	public String toString() {
		return string;
	}
}
