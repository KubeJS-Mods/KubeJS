package dev.latvian.kubejs.recipe.silentsmek;

import dev.latvian.kubejs.recipe.RegisterRecipeHandlersEvent;
import net.minecraft.resources.ResourceLocation;

/**
 * @author LatvianModder
 */
public class SilentsMechanismsRecipes {
	public static void registerRecipeHandlers(RegisterRecipeHandlersEvent event) {
		event.register(new ResourceLocation("silents_mechanisms:alloy_smelting"), SilentsMechanmismsAlloySmeltingRecipeJS::new);
	}
}