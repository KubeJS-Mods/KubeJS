package dev.latvian.mods.kubejs.util;

import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;

public interface Cast {
	@SuppressWarnings("unchecked")
	@Contract("null -> null; !null -> !null")
	static <T> @Nullable T to(@Nullable Object o) {
		return (T) o;
	}
}
