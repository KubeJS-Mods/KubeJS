package dev.latvian.mods.kubejs.util;

import com.google.common.collect.Sets;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.SerializationTags;
import net.minecraft.tags.TagCollection;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;

import java.util.Collection;

public class Tags {
	public static TagCollection<Item> items() {
		return SerializationTags.getInstance().getOrEmpty(Registry.ITEM_REGISTRY);
	}

	public static TagCollection<Block> blocks() {
		return SerializationTags.getInstance().getOrEmpty(Registry.BLOCK_REGISTRY);
	}

	public static TagCollection<Fluid> fluids() {
		return SerializationTags.getInstance().getOrEmpty(Registry.FLUID_REGISTRY);
	}

	public static TagCollection<EntityType<?>> entityTypes() {
		return SerializationTags.getInstance().getOrEmpty(Registry.ENTITY_TYPE_REGISTRY);
	}

	public static Collection<ResourceLocation> byItemStack(ItemStack stack) {
		return byItem(stack.getItem());
	}

	public static Collection<ResourceLocation> byItem(Item item) {
		return forType(item, items());
	}

	public static Collection<ResourceLocation> byBlockState(BlockState state) {
		return forType(state.getBlock(), blocks());
	}

	public static Collection<ResourceLocation> byBlock(Block block) {
		return forType(block, blocks());
	}

	public static Collection<ResourceLocation> byFluid(Fluid fluid) {
		return forType(fluid, fluids());
	}

	public static Collection<ResourceLocation> byEntity(Entity entity) {
		return forType(entity.getType(), entityTypes());
	}

	public static Collection<ResourceLocation> byEntityType(EntityType<?> entityType) {
		return forType(entityType, entityTypes());
	}

	public static <T> Collection<ResourceLocation> forType(T item, TagCollection<T> tags) {
		Collection<ResourceLocation> list = Sets.newHashSet();
		for (var entry : tags.getAllTags().entrySet()) {
			if (entry.getValue().contains(item)) {
				list.add(entry.getKey());
			}
		}
		return list;
	}
}
