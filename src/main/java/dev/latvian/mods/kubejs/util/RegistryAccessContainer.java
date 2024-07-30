package dev.latvian.mods.kubejs.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JavaOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import dev.latvian.mods.kubejs.bindings.RegistryWrapper;
import dev.latvian.mods.kubejs.recipe.CachedItemTagLookup;
import dev.latvian.mods.kubejs.recipe.CachedTagLookup;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import dev.latvian.mods.kubejs.script.KubeJSContext;
import dev.latvian.mods.kubejs.server.DataExport;
import dev.latvian.mods.rhino.Context;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.core.Holder;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class RegistryAccessContainer implements ICondition.IContext {
	public static final RegistryAccessContainer BUILTIN = new RegistryAccessContainer(RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY));

	// Still necessary because STARTUP and CLIENT scripts need to know about registries
	@ApiStatus.Internal
	public static RegistryAccessContainer current = BUILTIN;

	public static RegistryAccessContainer of(Context cx) {
		return cx instanceof KubeJSContext kcx ? kcx.getRegistries() : current;
	}

	private final RegistryAccess.Frozen access;
	private final RegistryOps<Tag> nbt;
	private final RegistryOps<JsonElement> json;
	private final RegistryOps<Object> java;
	private DamageSources damageSources;
	private final Map<String, ItemStack> itemStackParseCache;
	public final Map<ResourceKey<?>, CachedTagLookup.Entry<?>> cachedRegistryTags;
	public CachedItemTagLookup cachedItemTags;
	public CachedTagLookup<Block> cachedBlockTags;
	public CachedTagLookup<Fluid> cachedFluidTags;
	private Map<ResourceLocation, RegistryWrapper> cachedRegistryWrappers;

	public RegistryAccessContainer(RegistryAccess.Frozen access) {
		this.access = access;
		this.nbt = access.createSerializationContext(NbtOps.INSTANCE);
		this.json = access.createSerializationContext(JsonOps.INSTANCE);
		this.java = access.createSerializationContext(JavaOps.INSTANCE);
		this.damageSources = null;
		this.itemStackParseCache = new HashMap<>();
		this.cachedRegistryTags = new Reference2ObjectOpenHashMap<>();
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
	public synchronized <T> void cacheTags(Registry<T> registry, Map<ResourceLocation, List<TagLoader.EntryWithSource>> map) {
		var key1 = registry == null ? null : (ResourceKey) registry.key();

		if (key1 == null) {
			return;
		}

		try {
			if (key1 == Registries.ITEM) {
				cachedItemTags = Cast.to(new CachedItemTagLookup((Registry) registry, map));
				cachedRegistryTags.put(key1, new CachedTagLookup.Entry(key1, registry, cachedItemTags));
			} else if (key1 == Registries.BLOCK) {
				cachedBlockTags = Cast.to(new CachedTagLookup<>(registry, map));
				cachedRegistryTags.put(key1, new CachedTagLookup.Entry(key1, registry, cachedBlockTags));
			} else if (key1 == Registries.FLUID) {
				cachedFluidTags = Cast.to(new CachedTagLookup<>(registry, map));
				cachedRegistryTags.put(key1, new CachedTagLookup.Entry(key1, registry, cachedFluidTags));
			} else {
				cachedRegistryTags.put(key1, new CachedTagLookup.Entry(key1, registry, new CachedTagLookup<>(registry, map)));
			}
		} catch (Exception ex) {
			ConsoleJS.SERVER.error("Error caching tags for " + key1, ex);
		}

		if (DataExport.export != null) {
			var loc = "tags/" + key1.location() + "/";

			for (var entry : map.entrySet()) {
				var list = new ArrayList<String>();

				for (var e : entry.getValue()) {
					list.add(e.entry().toString());
				}

				list.sort(String.CASE_INSENSITIVE_ORDER);
				var arr = new JsonArray();

				for (var e : list) {
					arr.add(e);
				}

				DataExport.export.addJson(loc + entry.getKey() + ".json", arr);
			}
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

	private <T> RegistryWrapper<T> createRegistryWrapper(ResourceLocation id) {
		var key = ResourceKey.<T>createRegistryKey(id);
		return new RegistryWrapper<>(access.registryOrThrow(key), ResourceKey.create(key, ID.UNKNOWN));
	}

	public RegistryWrapper wrapRegistry(ResourceLocation id) {
		if (cachedRegistryWrappers == null) {
			cachedRegistryWrappers = new HashMap<>();
		}

		return cachedRegistryWrappers.computeIfAbsent(id, this::createRegistryWrapper);
	}

	@Override
	public <T> Map<ResourceLocation, Collection<Holder<T>>> getAllTags(ResourceKey<? extends Registry<T>> key) {
		var cached = cachedRegistryTags.get(key);

		if (cached != null) {
			return (Map) cached.lookup().tagMap();
		}

		return Map.of();
	}
}
