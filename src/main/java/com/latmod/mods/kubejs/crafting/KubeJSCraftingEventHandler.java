package com.latmod.mods.kubejs.crafting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.latmod.mods.kubejs.KubeJS;
import com.latmod.mods.kubejs.KubeJSEvents;
import com.latmod.mods.kubejs.events.EventsJS;
import com.latmod.mods.kubejs.item.IIngredientJS;
import com.latmod.mods.kubejs.item.ItemStackJS;
import com.latmod.mods.kubejs.util.UtilsJS;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = KubeJS.MOD_ID)
public class KubeJSCraftingEventHandler
{
	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void registerRecipesPre(RegistryEvent.Register<IRecipe> event)
	{
		MinecraftForge.EVENT_BUS.post(new CraftingHandlerRegistryEvent(RecipeHandlerRegistry.INSTANCE));
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public static void registerRecipesPost(RegistryEvent.Register<IRecipe> event)
	{
		EventsJS.INSTANCE.post(KubeJSEvents.RECIPES, new RecipeEventJS(RecipeHandlerRegistry.INSTANCE));
	}

	@SubscribeEvent
	public static void registerCraftingHandlers(CraftingHandlerRegistryEvent event)
	{
		event.register("crafting_table", new CraftingTableHandler());
		event.register("furnace", new FurnaceHandler());
	}

	public static class CraftingTableHandler implements IRecipeHandler
	{
		public void add(String recipeID, Object recipe)
		{
			JsonElement e = UtilsJS.INSTANCE.toJsonElement(recipe);

			System.out.println(e);

			if (e.isJsonObject())
			{
				JsonObject o = e.getAsJsonObject();

				try
				{
					IRecipe r = CraftingHelper.getRecipe(o, IIngredientJS.CONTEXT);

					if (r != null)
					{
						r.setRegistryName(new ResourceLocation(IIngredientJS.CONTEXT.appendModId(recipeID)));
						ForgeRegistries.RECIPES.register(r);
					}
				}
				catch (Exception ex)
				{
					KubeJS.LOGGER.warn("Failed to load a recipe with id '" + recipeID + "'!");
					ex.printStackTrace();
				}
			}
		}

		public void remove(String id)
		{
			//FIXME
		}

		public void remove(IIngredientJS output)
		{
			//FIXME
		}
	}

	public static class FurnaceHandler implements IRecipeHandler
	{
		public void add(ItemStackJS input, ItemStackJS output, float experience)
		{
			FurnaceRecipes.instance().addSmeltingRecipe(input.itemStack(), output.itemStack(), experience);
		}

		public void add(ItemStackJS input, ItemStackJS output)
		{
			add(input, output, 0F);
		}

		public void remove(IIngredientJS output)
		{
			FurnaceRecipes.instance().getSmeltingList().values().removeIf(stack -> output.test(new ItemStackJS.Bound(stack)));
		}

		public void removeInput(IIngredientJS input)
		{
			FurnaceRecipes.instance().getSmeltingList().keySet().removeIf(stack -> input.test(new ItemStackJS.Bound(stack)));
		}
	}
}