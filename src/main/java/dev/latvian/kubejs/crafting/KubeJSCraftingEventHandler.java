package dev.latvian.kubejs.crafting;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.crafting.handlers.CraftingTableRecipeEventJS;
import dev.latvian.kubejs.crafting.handlers.FurnaceRecipeEventJS;
import dev.latvian.kubejs.event.EventsJS;
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
		EventsJS.INSTANCE.post(KubeJSEvents.RECIPES_CRAFTING_TABLE, new CraftingTableRecipeEventJS(event.getRegistry()));
		EventsJS.INSTANCE.post(KubeJSEvents.RECIPES_FURNACE, new FurnaceRecipeEventJS());
	}
}