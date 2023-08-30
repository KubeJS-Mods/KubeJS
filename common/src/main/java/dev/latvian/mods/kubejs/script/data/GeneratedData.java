package dev.latvian.mods.kubejs.script.data;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.IoSupplier;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.function.Function;

public record GeneratedData(ResourceLocation id, byte[] data) implements IoSupplier<InputStream> {
	public static final Function<Map.Entry<ResourceLocation, byte[]>, ResourceLocation> KEY = Map.Entry::getKey;
	public static final Function<Map.Entry<ResourceLocation, byte[]>, GeneratedData> VALUE = e -> new GeneratedData(e.getKey(), e.getValue());

	@Override
	@NotNull
	public InputStream get() {
		return new ByteArrayInputStream(data);
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof GeneratedData g && id.equals(g.id);
	}

	@Override
	public String toString() {
		return id.toString();
	}
}
