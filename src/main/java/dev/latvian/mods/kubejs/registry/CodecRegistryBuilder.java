package dev.latvian.mods.kubejs.registry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult.Error;
import com.mojang.serialization.DynamicOps;
import dev.latvian.mods.kubejs.error.KubeRuntimeException;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.util.JsonUtils;
import net.minecraft.resources.Identifier;

public class CodecRegistryBuilder<T> extends BuilderBase<T> {
	private final DynamicOps<JsonElement> jsonOps;
	private final Codec<T> codec;
	private JsonElement json;

	public CodecRegistryBuilder(Identifier id, DynamicOps<JsonElement> jsonOps, Codec<T> codec) {
		super(id);
		this.jsonOps = jsonOps;
		this.codec = codec;
		this.json = new JsonObject();
	}

	@Info("""
		Returns the raw JSON payload used to create this registry object.
		""")
	public JsonObject json() {
		if (json instanceof JsonObject object) {
			return object;
		}

		var object = new JsonObject();
		json = object;
		return object;
	}

	@Info("""
		Sets the raw JSON payload used to create this registry object.
		""")
	public CodecRegistryBuilder<T> json(JsonElement json) {
		this.json = JsonUtils.copy(json);
		return this;
	}

	@Info("""
		Merges values into the current raw JSON payload used to create this registry object.
		""")
	public CodecRegistryBuilder<T> merge(JsonObject json) {
		var current = json();

		for (var entry : json.entrySet()) {
			current.add(entry.getKey(), entry.getValue());
		}

		return this;
	}

	@Override
	public T createObject() {
		var result = codec.parse(jsonOps, json);
		var value = result.result();

		if (value.isPresent()) {
			return value.get();
		}

		var registry = registryKey == null ? "<unknown registry>" : registryKey.identifier().toString();
		var message = result.error().map(Error::message).orElse("Unknown error");
		throw new KubeRuntimeException("Failed to parse registry object '%s' of '%s': %s".formatted(id, registry, message)).source(sourceLine);
	}
}
