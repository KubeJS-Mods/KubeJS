package dev.latvian.mods.kubejs.item;

import dev.architectury.registry.item.ItemPropertiesRegistry;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.event.StartupEventJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.kubejs.item.ingredient.MatchAllIngredientJS;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.resources.ResourceLocation;

public class ItemModelPropertiesEventJS extends StartupEventJS {
	public void register(IngredientJS ingredient, String overwriteId, ClampedItemPropertyFunction callback) {
		if (ingredient instanceof MatchAllIngredientJS) {
			registerAll(overwriteId, callback);
		} else {
			for (var stack : ingredient.getStacks()) {
				ItemPropertiesRegistry.register(stack.getItem(), new ResourceLocation(KubeJS.appendModId(overwriteId)), callback);
			}
		}
	}

	public void registerAll(String overwriteId, ClampedItemPropertyFunction callback) {
		ItemPropertiesRegistry.registerGeneric(new ResourceLocation(KubeJS.appendModId(overwriteId)), callback);
	}
}
