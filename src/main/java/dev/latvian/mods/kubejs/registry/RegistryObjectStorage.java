package dev.latvian.mods.kubejs.registry;

import com.mojang.serialization.Codec;
import dev.latvian.mods.kubejs.util.Cast;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class RegistryObjectStorage<T> implements Iterable<BuilderBase<? extends T>> {
	private static final Object LOCK = new Object();
	private static final Map<ResourceKey<? extends Registry<?>>, RegistryObjectStorage<?>> MAP = new Reference2ObjectOpenHashMap<>();
	public static final List<BuilderBase<?>> ALL_BUILDERS = new LinkedList<>();

	public static final Codec<RegistryObjectStorage<?>> CODEC = Identifier.CODEC.xmap(rl -> RegistryObjectStorage.of(ResourceKey.createRegistryKey(rl)), ri -> ri.key.identifier());

	public static <T> RegistryObjectStorage<T> of(ResourceKey<? extends Registry<T>> key) {
		synchronized (LOCK) {
			return Cast.to(MAP.computeIfAbsent(key, RegistryObjectStorage::new));
		}
	}

	public static final RegistryObjectStorage<Fluid> FLUID = of(Registries.FLUID);
	public static final RegistryObjectStorage<Block> BLOCK = of(Registries.BLOCK);
	public static final RegistryObjectStorage<Item> ITEM = of(Registries.ITEM);
	public static final RegistryObjectStorage<BlockEntityType<?>> BLOCK_ENTITY = of(Registries.BLOCK_ENTITY_TYPE);
	public static final RegistryObjectStorage<FluidType> FLUID_TYPE = of(NeoForgeRegistries.Keys.FLUID_TYPES);

	public final ResourceKey<? extends Registry<T>> key;
	public final Map<Identifier, BuilderBase<? extends T>> objects;

	private RegistryObjectStorage(ResourceKey key) {
		this.key = key;
		this.objects = new LinkedHashMap<>();
	}

	@Override
	public Iterator<BuilderBase<? extends T>> iterator() {
		return objects.values().iterator();
	}

	@Override
	public String toString() {
		return key.identifier().toString();
	}
}
