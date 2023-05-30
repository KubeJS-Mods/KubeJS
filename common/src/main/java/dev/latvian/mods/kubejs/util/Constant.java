package dev.latvian.mods.kubejs.util;

import java.util.function.Supplier;

public record Constant<T>(T value) implements Supplier<T> {
	@Override
	public T get() {
		return value;
	}
}
