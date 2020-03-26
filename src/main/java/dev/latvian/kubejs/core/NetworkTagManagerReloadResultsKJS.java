package dev.latvian.kubejs.core;

import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.server.ServerJS;
import dev.latvian.kubejs.server.TagEventJS;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;

/**
 * @author LatvianModder
 */
public interface NetworkTagManagerReloadResultsKJS
{
	default <T> void customTagsKJS(Map<ResourceLocation, Tag.Builder<Block>> blocks, Map<ResourceLocation, Tag.Builder<Item>> items, Map<ResourceLocation, Tag.Builder<Fluid>> fluids, Map<ResourceLocation, Tag.Builder<EntityType<?>>> entityTypes)
	{
		if (ServerJS.instance != null)
		{
			new TagEventJS<>("blocks", blocks, UtilsJS.valueGetter(ForgeRegistries.BLOCKS, Blocks.AIR)).post(KubeJSEvents.BLOCK_TAGS);
			new TagEventJS<>("items", items, UtilsJS.valueGetter(ForgeRegistries.ITEMS, Items.AIR)).post(KubeJSEvents.ITEM_TAGS);
			new TagEventJS<>("fluids", fluids, UtilsJS.valueGetter(ForgeRegistries.FLUIDS, Fluids.EMPTY)).post(KubeJSEvents.FLUID_TAGS);
			new TagEventJS<>("entity_types", entityTypes, UtilsJS.valueGetter(ForgeRegistries.ENTITIES, null)).post(KubeJSEvents.ENTITY_TYPE_TAGS);
		}
	}
}