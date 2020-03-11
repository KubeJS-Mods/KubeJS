package dev.latvian.kubejs.server;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.NetworkTagCollection;
import net.minecraft.tags.NetworkTagManager;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author LatvianModder
 */
public class TagGroup<T>
{
	@SuppressWarnings("deprecation")
	public static final List<TagGroup<?>> GROUP_LIST = Util.make(new ArrayList<>(), list -> {
		list.add(new TagGroup<>("blocks", () -> Registry.BLOCK, NetworkTagManager::getBlocks, BlockTags::setCollection));
		list.add(new TagGroup<>("items", () -> Registry.ITEM, NetworkTagManager::getItems, ItemTags::setCollection));
		list.add(new TagGroup<>("fluids", () -> Registry.FLUID, NetworkTagManager::getFluids, FluidTags::setCollection));
		list.add(new TagGroup<>("entity_types", () -> Registry.ENTITY_TYPE, NetworkTagManager::getEntityTypes, EntityTypeTags::setCollection));
	});

	public final String name;
	public final Supplier<Registry<T>> registrySupplier;
	public final Function<NetworkTagManager, NetworkTagCollection<T>> collectionGetter;
	public final Consumer<NetworkTagCollection<T>> collectionSetter;

	public TagGroup(String n, Supplier<Registry<T>> r, Function<NetworkTagManager, NetworkTagCollection<T>> g, Consumer<NetworkTagCollection<T>> s)
	{
		name = n;
		registrySupplier = r;
		collectionGetter = g;
		collectionSetter = s;
	}

	@Override
	public String toString()
	{
		return name;
	}
}