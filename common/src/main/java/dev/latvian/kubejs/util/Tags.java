package dev.latvian.kubejs.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.SerializationTags;
import net.minecraft.tags.TagCollection;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

import java.util.Collection;

public class Tags
{
	public static TagCollection<Item> items()
	{
		return SerializationTags.getInstance().getItems();
	}

	public static TagCollection<Block> blocks()
	{
		return SerializationTags.getInstance().getBlocks();
	}

	public static TagCollection<Fluid> fluids()
	{
		return SerializationTags.getInstance().getFluids();
	}

	public static TagCollection<EntityType<?>> entityTypes()
	{
		return SerializationTags.getInstance().getEntityTypes();
	}

	public static Collection<ResourceLocation> byItemStack(ItemStack stack)
	{
		return items().getMatchingTags(stack.getItem());
	}

	public static Collection<ResourceLocation> byItem(Item item)
	{
		return items().getMatchingTags(item);
	}
}
