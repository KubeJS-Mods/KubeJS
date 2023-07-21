package dev.latvian.mods.kubejs.util;

import dev.latvian.mods.kubejs.item.ingredient.TagContext;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
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
		return generic(id, Registry.ITEM_REGISTRY);
	}

	public static TagKey<Block> block(ResourceLocation id) {
		return generic(id, Registry.BLOCK_REGISTRY);
	}

	public static TagKey<Fluid> fluid(ResourceLocation id) {
		return generic(id, Registry.FLUID_REGISTRY);
	}

	public static TagKey<EntityType<?>> entityType(ResourceLocation id) {
		return generic(id, Registry.ENTITY_TYPE_REGISTRY);
	}

	public static TagKey<Biome> biome(ResourceLocation id) {
		return generic(id, Registry.BIOME_REGISTRY);
	}

	public static Stream<TagKey<Item>> byItemStack(ItemStack stack) {
		return byItem(stack.getItem());
	}

	public static Stream<TagKey<Item>> byItem(Item item) {
		return forHolder(item.builtInRegistryHolder());
	}

	public static Stream<TagKey<Block>> byBlockState(BlockState state) {
		return byBlock(state.getBlock());
	}

	public static Stream<TagKey<Block>> byBlock(Block block) {
		return forHolder(block.builtInRegistryHolder());
	}

	public static Stream<TagKey<Fluid>> byFluid(Fluid fluid) {
		return forHolder(fluid.builtInRegistryHolder());
	}

	public static Stream<TagKey<EntityType<?>>> byEntity(Entity entity) {
		return byEntityType(entity.getType());
	}

	public static Stream<TagKey<EntityType<?>>> byEntityType(EntityType<?> entityType) {
		return forHolder(entityType.builtInRegistryHolder());
	}

	public static <T> Stream<TagKey<T>> forType(T object, Registry<T> registry) {
		warnIfUnbound();
		return registry.getResourceKey(object)
			.flatMap(registry::getHolder)
			.stream()
			.flatMap(Holder::tags);
	}

	private static <T> TagKey<T> generic(ResourceLocation id, ResourceKey<Registry<T>> registry) {
		return TagKey.create(registry, id);
	}

	private static <T> Stream<TagKey<T>> forHolder(Holder.Reference<T> registryHolder) {
		warnIfUnbound();
		return registryHolder.tags();
	}

	private static void warnIfUnbound() {
		if (!TagContext.INSTANCE.getValue().areTagsBound()) {
			ConsoleJS.getCurrent(ConsoleJS.STARTUP).warn("Tags have not been bound to registry yet! The values returned by this method may be outdated!", new Throwable());
		}
	}
}
