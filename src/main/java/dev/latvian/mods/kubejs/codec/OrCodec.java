package dev.latvian.mods.kubejs.codec;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record OrCodec<V>(List<Codec<V>> codecs) implements Codec<V> {
	@Override
	public <T> DataResult<Pair<V, T>> decode(DynamicOps<T> ops, T input) {
		for (int i = 0; i < codecs.size() - 1; i++) {
			var result = codecs.get(i).decode(ops, input);

			if (result.error().isEmpty()) {
				return result;
			}
		}

		return codecs.getLast().decode(ops, input);
	}

	@Override
	public <T> DataResult<T> encode(V input, DynamicOps<T> ops, T prefix) {
		for (int i = 0; i < codecs.size() - 1; i++) {
			var result = codecs.get(i).encode(input, ops, prefix);

			if (result.error().isEmpty()) {
				return result;
			}
		}

		return codecs.getLast().encode(input, ops, prefix);
	}

	@Override
	public @NotNull String toString() {
		return "OrCodec" + codecs;
	}
}
