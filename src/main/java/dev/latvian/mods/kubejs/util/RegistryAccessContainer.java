package dev.latvian.mods.kubejs.util;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JavaOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import dev.latvian.mods.rhino.Context;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public record RegistryAccessContainer(
	RegistryAccess.Frozen access,
	RegistryOps<Tag> nbt,
	RegistryOps<JsonElement> json,
	RegistryOps<Object> java,
	Lazy<DamageSources> damageSources,
	Map<String, ItemStack> itemStackParseCache
) {
	public static final RegistryAccessContainer BUILTIN = new RegistryAccessContainer(RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY));

	// Still necessary because STARTUP and CLIENT scripts need to know about registries
	public static RegistryAccessContainer current = BUILTIN;

	public RegistryAccessContainer(RegistryAccess.Frozen registryAccess) {
		this(
			registryAccess,
			registryAccess.createSerializationContext(NbtOps.INSTANCE),
			registryAccess.createSerializationContext(JsonOps.INSTANCE),
			registryAccess.createSerializationContext(JavaOps.INSTANCE),
			Lazy.of(() -> new DamageSources(registryAccess)),
			new HashMap<>()
		);
	}

	public <T> T decodeJson(Codec<T> codec, JsonElement from) {
		return codec.decode(json, from).result().orElseThrow().getFirst();
	}

	public <T> T decodeNbt(Codec<T> codec, Tag from) {
		return codec.decode(nbt, from).result().orElseThrow().getFirst();
	}

	public <T> T decodeJson(MapCodec<T> mapCodec, JsonElement from) {
		return mapCodec.decode(json, json.getMap(from).getOrThrow()).getOrThrow();
	}

	public <T> T decode(Context cx, Codec<T> codec, Object o) {
		if (o instanceof Tag tag) {
			return codec.decode(nbt, tag).result().orElseThrow().getFirst();
		} else if (o instanceof Map<?, ?> map) {
			return codec.decode(java, o).result().orElseThrow().getFirst();
		} else {
			return codec.decode(json, MapJS.json(cx, o)).result().orElseThrow().getFirst();
		}
	}

	public <T> T decodeMap(Context cx, MapCodec<T> codec, Object o) {
		if (o instanceof Tag tag) {
			return codec.decode(nbt, nbt.getMap(tag).getOrThrow()).result().orElseThrow();
		} else if (o instanceof Map<?, ?> map) {
			return codec.decode(java, java.getMap(map).getOrThrow()).result().orElseThrow();
		} else {
			return codec.decode(json, json.getMap(MapJS.json(cx, o)).getOrThrow()).result().orElseThrow();
		}
	}
}
