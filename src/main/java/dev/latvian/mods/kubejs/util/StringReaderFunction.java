package dev.latvian.mods.kubejs.util;

import com.mojang.brigadier.StringReader;

@FunctionalInterface
public interface StringReaderFunction<T> {
	T read(StringReader reader) throws Exception;
}
