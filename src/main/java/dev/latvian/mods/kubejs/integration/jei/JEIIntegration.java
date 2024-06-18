package dev.latvian.mods.kubejs.integration.jei;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.event.EventGroupRegistry;
import dev.latvian.mods.kubejs.recipe.viewer.RecipeViewerEntryType;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.neoforge.NeoForgeTypes;
import org.jetbrains.annotations.Nullable;

public class JEIIntegration implements KubeJSPlugin {
	@Override
	public void registerEvents(EventGroupRegistry registry) {
		registry.register(JEIEvents.GROUP);
	}

	@Nullable
	public static IIngredientType<?> typeOf(RecipeViewerEntryType type) {
		if (type == RecipeViewerEntryType.ITEM) {
			return VanillaTypes.ITEM_STACK;
		} else if (type == RecipeViewerEntryType.FLUID) {
			return NeoForgeTypes.FLUID_STACK;
		} else {
			return null;
		}
	}
}