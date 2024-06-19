package dev.latvian.mods.kubejs.util;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JavaOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import dev.latvian.mods.kubejs.recipe.CachedTagLookup;
import dev.latvian.mods.rhino.Context;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagLoader;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class RegistryAccessContainer {
	public static final RegistryAccessContainer BUILTIN = new RegistryAccessContainer(RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY));

	// Still necessary because STARTUP and CLIENT scripts need to know about registries
	@ApiStatus.Internal
	public static RegistryAccessContainer current = BUILTIN;

	private final RegistryAccess.Frozen access;
	private final RegistryOps<Tag> nbt;
	private final RegistryOps<JsonElement> json;
	private final RegistryOps<Object> java;
	private DamageSources damageSources;
	private final Map<String, ItemStack> itemStackParseCache;
	public CachedTagLookup<Item> cachedItemTags;
	public CachedTagLookup<Item> cachedBlockTags;
	public CachedTagLookup<Item> cachedFluidTags;

	public RegistryAccessContainer(RegistryAccess.Frozen access) {
		this.access = access;
		this.nbt = access.createSerializationContext(NbtOps.INSTANCE);
		this.json = access.createSerializationContext(JsonOps.INSTANCE);
		this.java = access.createSerializationContext(JavaOps.INSTANCE);
		this.damageSources = null;
		this.itemStackParseCache = new HashMap<>();
	}

	public RegistryAccess.Frozen access() {
		return access;
	}

	public RegistryOps<Tag> nbt() {
		return nbt;
	}

	public RegistryOps<JsonElement> json() {
		return json;
	}

	public RegistryOps<Object> java() {
		return java;
	}

	public DamageSources damageSources() {
		if (damageSources == null) {
			damageSources = new DamageSources(access);
		}

		return damageSources;
	}

	public Map<String, ItemStack> itemStackParseCache() {
		return itemStackParseCache;
	}

	// Currently this is the best way I can think of to have tags available at the time of recipe loading
	public <T> void cacheTags(Registry<T> registry, Map<ResourceLocation, List<TagLoader.EntryWithSource>> map) {
		var key1 = (ResourceKey) registry.key();

		if (key1 == Registries.ITEM) {
			cachedItemTags = Cast.to(new CachedTagLookup<>(registry, map));
		} else if (key1 == Registries.BLOCK) {
			cachedBlockTags = Cast.to(new CachedTagLookup<>(registry, map));
		} else if (key1 == Registries.FLUID) {
			cachedFluidTags = Cast.to(new CachedTagLookup<>(registry, map));
		}
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
		} else if (o instanceof Map<?, ?>) {
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
