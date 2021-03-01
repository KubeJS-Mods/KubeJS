package dev.latvian.kubejs.recipe.silentsmek;

import dev.latvian.kubejs.recipe.RegisterRecipeHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * @author LatvianModder
 */
public class SilentsMechanismsRecipes {
	@SubscribeEvent
	public static void registerRecipeHandlers(RegisterRecipeHandlersEvent event) {
		event.register("silents_mechanisms:alloy_smelting", SilentsMechanmismsAlloySmeltingRecipeJS::new);
	}
}