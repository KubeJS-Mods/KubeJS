package dev.latvian.mods.kubejs.util;

import dev.latvian.mods.kubejs.script.KubeJSContext;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;

import java.util.Objects;

public record WithContext<T>(KubeJSContext cx, T value) {
	public static WithContext of(Context cx, Object o, TypeInfo target) {
		if (cx instanceof KubeJSContext kcx) {
			var type = target.param(0);

			if (type.shouldConvert()) {
				return new WithContext(kcx, cx.jsToJava(o, type));
			} else {
				return new WithContext(kcx, o);
			}
		}

		throw new IllegalArgumentException("Context is not a KubeJSContext");
	}

	@Override
	public int hashCode() {
		return value == null ? 0 : value.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof WithContext<?> wc && Objects.equals(value, wc.value);
	}

	@Override
	public String toString() {
		return "WithContext[" + value + ']';
	}
}
