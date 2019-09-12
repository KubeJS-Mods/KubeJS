package dev.latvian.kubejs.crafting;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.event.EventsJS;
import dev.latvian.kubejs.item.ingredient.MatchAnyIngredientJS;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistry;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = KubeJS.MOD_ID)
public class KubeJSCraftingEventHandler
{
	@SubscribeEvent(priority = EventPriority.LOW)
	public static void registerRecipes(RegistryEvent.Register<IRecipe> event)
	{
		EventsJS.post(KubeJSEvents.RECIPES_CRAFTING_TABLE, new CraftingTableRecipeEventJS(event.getRegistry()));
		EventsJS.post(KubeJSEvents.RECIPES_FURNACE, new FurnaceRecipeEventJS("minecraft"));

		if (event.getRegistry() instanceof ForgeRegistry)
		{
			ForgeRegistry<IRecipe> r = (ForgeRegistry<IRecipe>) event.getRegistry();

			MatchAnyIngredientJS out = new MatchAnyIngredientJS();
			EventsJS.post(KubeJSEvents.RECIPES_REMOVE_OUTPUT, new RemoveRecipesEventJS("minecraft", "crafting_table", out));

			if (!out.isEmpty())
			{
				boolean frozen = r.isLocked();

				if (frozen)
				{
					r.unfreeze();
				}

				List<IRecipe> recipes = new ArrayList<>(r.getValuesCollection());

				for (IRecipe recipe : recipes)
				{
					if (out.test(recipe.getRecipeOutput()))
					{
						r.remove(recipe.getRegistryName());
					}
				}

				if (frozen)
				{
					r.freeze();
				}
			}
		}

		MatchAnyIngredientJS furnaceOutput = new MatchAnyIngredientJS();
		EventsJS.post(KubeJSEvents.RECIPES_REMOVE_OUTPUT, new RemoveRecipesEventJS("minecraft", "furnace", furnaceOutput));

		if (!furnaceOutput.isEmpty())
		{
			FurnaceRecipes.instance().getSmeltingList().values().removeIf(furnaceOutput);
		}

		MatchAnyIngredientJS furnaceInput = new MatchAnyIngredientJS();
		EventsJS.post(KubeJSEvents.RECIPES_REMOVE_INPUT, new RemoveRecipesEventJS("minecraft", "furnace", furnaceInput));

		if (!furnaceInput.isEmpty())
		{
			FurnaceRecipes.instance().getSmeltingList().keySet().removeIf(furnaceInput);
		}
	}
}