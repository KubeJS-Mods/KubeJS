package dev.latvian.mods.kubejs.registry;

import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.util.Cast;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class RegistryObjectStorage<T> implements Iterable<BuilderBase<? extends T>> {
	private static final Object LOCK = new Object();
	private static final Map<ResourceKey<? extends Registry<?>>, RegistryObjectStorage<?>> MAP = new IdentityHashMap<>();
	public static final List<BuilderBase<?>> ALL_BUILDERS = new LinkedList<>();

	public static final Codec<RegistryObjectStorage<?>> CODEC = ResourceLocation.CODEC.xmap(rl -> RegistryObjectStorage.of(ResourceKey.createRegistryKey(rl)), ri -> ri.key.location());

	public static <T> RegistryObjectStorage<T> of(ResourceKey<Registry<T>> key) {
		synchronized (LOCK) {
			return Cast.to(MAP.computeIfAbsent(key, RegistryObjectStorage::new));
		}
	}

	public static final RegistryObjectStorage<Fluid> FLUID = of(Registries.FLUID);
	public static final RegistryObjectStorage<Block> BLOCK = of(Registries.BLOCK);
	public static final RegistryObjectStorage<Item> ITEM = of(Registries.ITEM);

	public final ResourceKey<Registry<T>> key;
	public final Map<ResourceLocation, BuilderBase<? extends T>> objects;

	private RegistryObjectStorage(ResourceKey key) {
		this.key = key;
		this.objects = new LinkedHashMap<>();
	}

	@NotNull
	@Override
	public Iterator<BuilderBase<? extends T>> iterator() {
		return objects.values().iterator();
	}
}
