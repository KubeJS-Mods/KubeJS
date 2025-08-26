package dev.latvian.mods.kubejs.util;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JavaOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import dev.latvian.mods.rhino.Context;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;

import java.util.Map;

public class OpsContainer {
	public static final OpsContainer DEFAULT = new OpsContainer(NbtOps.INSTANCE, JsonOps.INSTANCE, JavaOps.INSTANCE);

	private final DynamicOps<Tag> nbt;
	private final DynamicOps<JsonElement> json;
	private final DynamicOps<Object> java;

	public OpsContainer(DynamicOps<Tag> nbt, DynamicOps<JsonElement> json, DynamicOps<Object> java) {
		this.nbt = nbt;
		this.json = json;
		this.java = java;
	}

	public DynamicOps<Tag> nbt() {
		return nbt;
	}

	public DynamicOps<JsonElement> json() {
		return json;
	}

	public DynamicOps<Object> java() {
		return java;
	}

	public <T> T decode(Context cx, Codec<T> codec, Object o) {
		return (switch (o) {
			case Tag tag -> codec.decode(nbt, tag);
			case Map<?, ?> map -> codec.decode(java, map);
			default -> codec.decode(json, JsonUtils.of(cx, o));
		}).getOrThrow().getFirst();
	}

	public <T> T decodeMap(Context cx, MapCodec<T> codec, Object o) {
		return (switch (o) {
			case Tag tag -> codec.decode(nbt, nbt.getMap(tag).getOrThrow());
			case Map<?, ?> map -> codec.decode(java, java.getMap(map).getOrThrow());
			default -> codec.decode(json, json.getMap(JsonUtils.of(cx, o)).getOrThrow());
		}).getOrThrow();
	}
}
