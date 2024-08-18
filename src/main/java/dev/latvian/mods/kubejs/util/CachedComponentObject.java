package dev.latvian.mods.kubejs.util;

import dev.latvian.mods.kubejs.core.RegistryObjectKJS;
import io.netty.buffer.Unpooled;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;

import java.util.UUID;

public record CachedComponentObject<T extends RegistryObjectKJS<T>>(UUID cacheKey, T value, DataComponentPatch components) {
	public static <T extends RegistryObjectKJS<T>> CachedComponentObject<T> of(T value, DataComponentPatch components) {
		var buf = new FriendlyByteBuf(Unpooled.buffer());
		buf.writeUtf(value.kjs$getId());
		buf.writeVarInt(components.size());

		for (var entry : components.entrySet()) {
			var key = BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(entry.getKey());
			buf.writeUtf(key.getNamespace());
			buf.writeUtf(key.getPath());
			buf.writeBoolean(entry.getValue().isPresent());

			if (entry.getValue().isPresent()) {
				buf.writeVarInt(entry.getValue().get().hashCode());
			}
		}

		return new CachedComponentObject<>(UUID.nameUUIDFromBytes(buf.array()), value, components);
	}

	@Override
	public boolean equals(Object o) {
		return this == o || o instanceof CachedComponentObject<?> c && value == c.value && components.equals(c.components);
	}
}
