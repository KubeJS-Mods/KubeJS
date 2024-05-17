package dev.latvian.mods.kubejs.util;

import dev.latvian.mods.kubejs.script.KubeJSContext;

import java.util.Objects;

public record WithContext<T>(KubeJSContext cx, T value) {
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
