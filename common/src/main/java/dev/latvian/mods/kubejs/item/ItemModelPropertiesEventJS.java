package dev.latvian.mods.kubejs.item;

import dev.architectury.registry.item.ItemPropertiesRegistry;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.event.StartupEventJS;
import dev.latvian.mods.kubejs.item.ingredient.WildcardIngredient;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

public class ItemModelPropertiesEventJS extends StartupEventJS {
	public void register(Ingredient ingredient, String overwriteId, ClampedItemPropertyFunction callback) {
		if (ingredient instanceof WildcardIngredient) {
			registerAll(overwriteId, callback);
		} else {
			for (var stack : ingredient.kjs$getStacks()) {
				ItemPropertiesRegistry.register(stack.getItem(), new ResourceLocation(KubeJS.appendModId(overwriteId)), callback);
			}
		}
	}

	public void registerAll(String overwriteId, ClampedItemPropertyFunction callback) {
		ItemPropertiesRegistry.registerGeneric(new ResourceLocation(KubeJS.appendModId(overwriteId)), callback);
	}
}
