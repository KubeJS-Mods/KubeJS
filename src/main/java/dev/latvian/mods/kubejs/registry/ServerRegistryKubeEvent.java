package dev.latvian.mods.kubejs.registry;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.kubejs.script.SourceLine;
import dev.latvian.mods.kubejs.util.ID;
import dev.latvian.mods.rhino.Context;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import java.util.List;
import java.util.function.Supplier;

public class ServerRegistryKubeEvent<T> implements KubeEvent {
	public final ResourceKey<Registry<T>> registryKey;
	private final BuilderTypeRegistryHandler.Info<T> builderInfo;
	public final DynamicOps<JsonElement> jsonOps;
	public final Codec<T> codec;
	private final List<BuilderBase<?>> builders;

	public ServerRegistryKubeEvent(ResourceKey<Registry<T>> registryKey, DynamicOps<JsonElement> jsonOps, Codec<T> codec, List<BuilderBase<?>> builders) {
		this.registryKey = registryKey;
		this.builderInfo = BuilderTypeRegistryHandler.info(registryKey);
		this.jsonOps = jsonOps;
		this.codec = codec;
		this.builders = builders;
	}

	public BuilderBase<? extends T> create(Context cx, String id, String type) {
		var sourceLine = SourceLine.of(cx);

		var t = builderInfo.namedType(type);

		if (t == null) {
			throw new IllegalArgumentException("Unknown type '" + type + "' for object '" + id + "'!");
		}

		var b = t.factory().createBuilder(ID.kjs(id));

		if (b == null) {
			throw new KubeRuntimeException("Unknown type '" + type + "' for object '" + id + "'!").source(sourceLine);
		} else {
			b.sourceLine = sourceLine;
			b.registryKey = registryKey;
			builders.add(b);
			return b;
		}
	}

	public BuilderBase<? extends T> create(Context cx, String id) {
		var sourceLine = SourceLine.of(cx);
		var t = builderInfo.defaultType();

		if (t == null) {
			throw new KubeRuntimeException("Registry '" + registryKey.location() + "' doesn't have a default type registered!").source(sourceLine);
		}

		var b = t.factory().createBuilder(ID.kjs(id));

		if (b == null) {
			throw new KubeRuntimeException("Unknown type '" + t.type() + "' for object '" + id + "'!").source(sourceLine);
		} else {
			b.sourceLine = sourceLine;
			b.registryKey = registryKey;
			builders.add(b);
			return b;
		}
	}

	public CustomBuilderObject createCustom(Context cx, String id, Supplier<Object> object) {
		var sourceLine = SourceLine.of(cx);

		if (object == null) {
			throw new KubeRuntimeException("Tried to register a null object with id: " + id).source(sourceLine);
		}

		var b = new CustomBuilderObject(ID.kjs(id), object);
		b.sourceLine = sourceLine;
		b.registryKey = registryKey;
		builders.add(b);
		return b;
	}

	public CustomBuilderObject createFromJson(Context cx, String id, JsonElement json) {
		var sourceLine = SourceLine.of(cx);

		var b = new CustomBuilderObject(ID.kjs(id), () -> codec.parse(jsonOps, json).result().orElseThrow());
		b.sourceLine = sourceLine;
		b.registryKey = registryKey;
		builders.add(b);
		return b;
	}
}