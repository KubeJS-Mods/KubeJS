package dev.latvian.mods.kubejs.util;

import dev.latvian.mods.kubejs.item.ingredient.TagContext;
import dev.latvian.mods.rhino.Context;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;

import java.util.stream.Stream;

public class Tags {
	public static TagKey<Item> item(ResourceLocation id) {
		return generic(id, Registries.ITEM);
	}

	public static TagKey<Block> block(ResourceLocation id) {
		return generic(id, Registries.BLOCK);
	}

	public static TagKey<Fluid> fluid(ResourceLocation id) {
		return generic(id, Registries.FLUID);
	}

	public static TagKey<EntityType<?>> entityType(ResourceLocation id) {
		return generic(id, Registries.ENTITY_TYPE);
	}

	public static TagKey<Biome> biome(ResourceLocation id) {
		return generic(id, Registries.BIOME);
	}

	public static Stream<TagKey<Item>> byItemStack(Context cx, ItemStack stack) {
		return byItem(cx, stack.getItem());
	}

	public static Stream<TagKey<Item>> byItem(Context cx, Item item) {
		return forHolder(cx, item.builtInRegistryHolder());
	}

	public static Stream<TagKey<Block>> byBlockState(Context cx, BlockState state) {
		return byBlock(cx, state.getBlock());
	}

	public static Stream<TagKey<Block>> byBlock(Context cx, Block block) {
		return forHolder(cx, block.builtInRegistryHolder());
	}

	public static Stream<TagKey<Fluid>> byFluid(Context cx, Fluid fluid) {
		return forHolder(cx, fluid.builtInRegistryHolder());
	}

	public static Stream<TagKey<EntityType<?>>> byEntity(Context cx, Entity entity) {
		return byEntityType(cx, entity.getType());
	}

	public static Stream<TagKey<EntityType<?>>> byEntityType(Context cx, EntityType<?> entityType) {
		return forHolder(cx, entityType.builtInRegistryHolder());
	}

	public static <T> Stream<TagKey<T>> forType(Context cx, T object, Registry<T> registry) {
		warnIfUnbound(cx);
		return registry.getResourceKey(object)
			.flatMap(registry::getHolder)
			.stream()
			.flatMap(Holder::tags);
	}

	private static <T> TagKey<T> generic(ResourceLocation id, ResourceKey<Registry<T>> registry) {
		return TagKey.create(registry, id);
	}

	private static <T> Stream<TagKey<T>> forHolder(Context cx, Holder.Reference<T> registryHolder) {
		warnIfUnbound(cx);
		return registryHolder.tags();
	}

	private static void warnIfUnbound(Context cx) {
		if (!TagContext.INSTANCE.getValue().areTagsBound()) {
			ConsoleJS.getCurrent(cx).warn("Tags have not been bound to registry yet! The values returned by this method may be outdated!", new Throwable());
		}
	}
}
