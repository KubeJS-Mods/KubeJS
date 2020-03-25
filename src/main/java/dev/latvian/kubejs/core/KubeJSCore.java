package dev.latvian.kubejs.core;

import com.google.gson.JsonObject;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.item.ItemFoodEatenEventJS;
import dev.latvian.kubejs.item.ItemJS;
import dev.latvian.kubejs.recipe.RecipeEventJS;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.server.ServerJS;
import dev.latvian.kubejs.server.TagEventJS;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author LatvianModder
 */
public class KubeJSCore
{
	public static <T> void customTags(Map<ResourceLocation, Tag.Builder<Block>> blocks, Map<ResourceLocation, Tag.Builder<Item>> items, Map<ResourceLocation, Tag.Builder<Fluid>> fluids, Map<ResourceLocation, Tag.Builder<EntityType<?>>> entityTypes)
	{
		if (ServerJS.instance != null)
		{
			new TagEventJS<>("blocks", blocks, valueGetter(ForgeRegistries.BLOCKS, Blocks.AIR)).post(KubeJSEvents.BLOCK_TAGS);
			new TagEventJS<>("items", items, valueGetter(ForgeRegistries.ITEMS, Items.AIR)).post(KubeJSEvents.ITEM_TAGS);
			new TagEventJS<>("fluids", fluids, valueGetter(ForgeRegistries.FLUIDS, Fluids.EMPTY)).post(KubeJSEvents.FLUID_TAGS);
			new TagEventJS<>("entity_types", entityTypes, valueGetter(ForgeRegistries.ENTITIES, null)).post(KubeJSEvents.ENTITY_TYPE_TAGS);
		}
	}

	private static <T extends IForgeRegistryEntry<T>> Function<ResourceLocation, Optional<T>> valueGetter(IForgeRegistry<T> registry, @Nullable T def)
	{
		return id -> {
			T value = registry.getValue(id);

			if (value != null && value != def)
			{
				return Optional.of(value);
			}

			return Optional.empty();
		};
	}

	public static void customRecipes(RecipeManager recipeManager, Map<ResourceLocation, JsonObject> jsonMap)
	{
		RecipeEventJS.instance.post(recipeManager, jsonMap);
		RecipeEventJS.instance = null;
	}

	public static void foodEaten(LivingEntity e, ItemStack is)
	{
		if (e instanceof ServerPlayerEntity)
		{
			ItemFoodEatenEventJS event = new ItemFoodEatenEventJS((ServerPlayerEntity) e, is);
			Item i = is.getItem();

			if (i instanceof ItemJS)
			{
				ItemJS j = (ItemJS) i;

				if (j.properties.foodBuilder != null && j.properties.foodBuilder.eaten != null)
				{
					j.properties.foodBuilder.eaten.accept(event);
				}
			}

			event.post(ScriptType.SERVER, KubeJSEvents.ITEM_FOOD_EATEN);
		}
	}
}