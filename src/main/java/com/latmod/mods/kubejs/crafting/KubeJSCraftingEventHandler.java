package com.latmod.mods.kubejs.crafting;

import com.latmod.mods.kubejs.KubeJS;
import com.latmod.mods.kubejs.KubeJSEvents;
import com.latmod.mods.kubejs.crafting.handlers.CraftingTableRecipeEventJS;
import com.latmod.mods.kubejs.crafting.handlers.FurnaceRecipeEventJS;
import com.latmod.mods.kubejs.events.EventsJS;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = KubeJS.MOD_ID)
public class KubeJSCraftingEventHandler
{
	@SubscribeEvent(priority = EventPriority.LOW)
	public static void registerRecipes(RegistryEvent.Register<IRecipe> event)
	{
		EventsJS.INSTANCE.post(KubeJSEvents.RECIPES_CRAFTING_TABLE, new CraftingTableRecipeEventJS());
		EventsJS.INSTANCE.post(KubeJSEvents.RECIPES_FURNACE, new FurnaceRecipeEventJS());
	}
}