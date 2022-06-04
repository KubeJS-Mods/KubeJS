package dev.latvian.mods.kubejs.core;

import org.jetbrains.annotations.Nullable;

public interface AsKJS<T> {
	T asKJS();

	@Nullable
	static <T> T wrapSafe(@Nullable AsKJS<T> as) {
		return as == null ? null : as.asKJS();
	}
}
